package com.notary.will.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notary.will.entity.PropertyInventory;
import com.notary.will.entity.User;
import com.notary.will.entity.WillCase;
import com.notary.will.enums.CaseStatus;
import com.notary.will.enums.PropertyType;
import com.notary.will.enums.UserRole;
import com.notary.will.repository.PropertyInventoryRepository;
import com.notary.will.repository.UserRepository;
import com.notary.will.repository.WillCaseRepository;
import com.notary.will.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CaseOwnershipTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WillCaseRepository willCaseRepository;

    @Autowired
    private PropertyInventoryRepository propertyInventoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private User applicant1;
    private User applicant2;
    private User reviewer;
    private WillCase case1;
    private WillCase case2;
    private PropertyInventory property1;
    private String token1;
    private String token2;
    private String reviewerToken;

    @BeforeEach
    void setUp() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        applicant1 = createUser("applicant1_" + uuid, UserRole.APPLICANT, "13800138001");
        applicant2 = createUser("applicant2_" + uuid, UserRole.APPLICANT, "13800138002");
        reviewer = createUser("reviewer1_" + uuid, UserRole.REVIEWER, "13900139001");

        case1 = createCase(applicant1.getId(), "张三的遗嘱");
        case2 = createCase(applicant2.getId(), "李四的遗嘱");

        property1 = createProperty(case1.getId(), "房产", PropertyType.REAL_ESTATE);

        token1 = jwtTokenProvider.generateToken(applicant1.getId(), applicant1.getUsername(), applicant1.getRole().name());
        token2 = jwtTokenProvider.generateToken(applicant2.getId(), applicant2.getUsername(), applicant2.getRole().name());
        reviewerToken = jwtTokenProvider.generateToken(reviewer.getId(), reviewer.getUsername(), reviewer.getRole().name());
    }

    private User createUser(String username, UserRole role, String phone) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(role);
        user.setPhone(phone);
        user.setEnabled(true);
        return userRepository.save(user);
    }

    private WillCase createCase(Long applicantId, String title) {
        WillCase willCase = new WillCase();
        willCase.setApplicantId(applicantId);
        willCase.setTitle(title);
        willCase.setStatus(CaseStatus.DRAFT);
        willCase.setCaseNumber("CASE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return willCaseRepository.save(willCase);
    }

    private PropertyInventory createProperty(Long caseId, String description, PropertyType type) {
        PropertyInventory property = new PropertyInventory();
        property.setCaseId(caseId);
        property.setType(type);
        property.setDescription(description);
        property.setValue(new BigDecimal("1000000"));
        return propertyInventoryRepository.save(property);
    }

    @Test
    @DisplayName("申请人可以访问自己的案件财产信息")
    void applicantCanAccessOwnCaseProperty() throws Exception {
        mockMvc.perform(get("/api/cases/" + case1.getId() + "/properties")
                        .header("Authorization", "Bearer " + token1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("申请人不能访问他人的案件财产信息")
    void applicantCannotAccessOtherCaseProperty() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/cases/" + case1.getId() + "/properties")
                        .header("Authorization", "Bearer " + token2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("403") || content.contains("AccessDenied"),
                "应该返回无权访问的错误信息，实际返回: " + content);
    }

    @Test
    @DisplayName("申请人不能修改他人的案件财产信息")
    void applicantCannotModifyOtherCaseProperty() throws Exception {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("description", "恶意修改的房产描述");
        updateData.put("type", "REAL_ESTATE");
        updateData.put("value", "999999");

        MvcResult result = mockMvc.perform(put("/api/cases/" + case1.getId() + "/properties/" + property1.getId())
                        .header("Authorization", "Bearer " + token2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isForbidden())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("403") || content.contains("AccessDenied"),
                "应该返回无权访问的错误信息，实际返回: " + content);

        PropertyInventory savedProperty = propertyInventoryRepository.findById(property1.getId()).orElseThrow();
        assertEquals("房产", savedProperty.getDescription(), "财产描述不应该被修改");
    }

    @Test
    @DisplayName("申请人不能为他人案件添加财产")
    void applicantCannotAddPropertyToOtherCase() throws Exception {
        Map<String, Object> newProperty = new HashMap<>();
        newProperty.put("type", "REAL_ESTATE");
        newProperty.put("description", "恶意添加的房产");
        newProperty.put("value", "500000");

        MvcResult result = mockMvc.perform(post("/api/cases/" + case1.getId() + "/properties")
                        .header("Authorization", "Bearer " + token2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProperty)))
                .andExpect(status().isForbidden())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("403") || content.contains("AccessDenied"),
                "应该返回无权访问的错误信息，实际返回: " + content);

        long propertyCount = propertyInventoryRepository.findByCaseId(case1.getId()).size();
        assertEquals(1, propertyCount, "不应该添加新的财产");
    }

    @Test
    @DisplayName("材料审核员可以访问所有案件的财产信息")
    void reviewerCanAccessAllCaseProperty() throws Exception {
        mockMvc.perform(get("/api/cases/" + case1.getId() + "/properties")
                        .header("Authorization", "Bearer " + reviewerToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/api/cases/" + case2.getId() + "/properties")
                        .header("Authorization", "Bearer " + reviewerToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("申请人不能访问他人的身份信息")
    void applicantCannotAccessOtherCaseIdentity() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/cases/" + case1.getId() + "/identity")
                        .header("Authorization", "Bearer " + token2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("403") || content.contains("AccessDenied"),
                "应该返回无权访问的错误信息，实际返回: " + content);
    }

    @Test
    @DisplayName("申请人不能访问他人的亲属关系")
    void applicantCannotAccessOtherCaseKinship() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/cases/" + case1.getId() + "/kinship")
                        .header("Authorization", "Bearer " + token2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("403") || content.contains("AccessDenied"),
                "应该返回无权访问的错误信息，实际返回: " + content);
    }

    @Test
    @DisplayName("申请人不能访问他人的受益人信息")
    void applicantCannotAccessOtherCaseBeneficiaries() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/cases/" + case1.getId() + "/beneficiaries")
                        .header("Authorization", "Bearer " + token2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("403") || content.contains("AccessDenied"),
                "应该返回无权访问的错误信息，实际返回: " + content);
    }

    @Test
    @DisplayName("申请人不能访问他人的见证人信息")
    void applicantCannotAccessOtherCaseWitnesses() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/cases/" + case1.getId() + "/witnesses")
                        .header("Authorization", "Bearer " + token2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("403") || content.contains("AccessDenied"),
                "应该返回无权访问的错误信息，实际返回: " + content);
    }

    @Test
    @DisplayName("申请人不能访问他人的材料信息")
    void applicantCannotAccessOtherCaseMaterials() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/cases/" + case1.getId() + "/materials")
                        .header("Authorization", "Bearer " + token2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("403") || content.contains("AccessDenied"),
                "应该返回无权访问的错误信息，实际返回: " + content);
    }

    @Test
    @DisplayName("未登录用户不能访问任何案件数据")
    void unauthorizedUserCannotAccessCaseData() throws Exception {
        mockMvc.perform(get("/api/cases/" + case1.getId() + "/properties")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/cases/" + case1.getId() + "/identity")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
