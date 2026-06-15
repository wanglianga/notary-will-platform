package com.notary.will.init;

import com.notary.will.entity.*;
import com.notary.will.enums.*;
import com.notary.will.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final WillCaseRepository willCaseRepository;
    private final IdentityInfoRepository identityInfoRepository;
    private final PropertyInventoryRepository propertyInventoryRepository;
    private final KinshipRelationRepository kinshipRelationRepository;
    private final HealthDeclarationRepository healthDeclarationRepository;
    private final AppointmentSlotRepository appointmentSlotRepository;
    private final InterviewRecordRepository interviewRecordRepository;
    private final WitnessVideoRepository witnessVideoRepository;
    private final MaterialItemRepository materialItemRepository;
    private final ArchiveRecordRepository archiveRecordRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final WitnessRepository witnessRepository;
    private final SupplementItemRepository supplementItemRepository;
    private final FeeRecordRepository feeRecordRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        createUsers();
        createSampleCases();
        createSampleData();
    }

    private void createUsers() {
        createUser("applicant1", "申请人张三", UserRole.APPLICANT, "13800000001");
        createUser("applicant2", "申请人李四", UserRole.APPLICANT, "13800000002");
        createUser("notary1", "公证员王五", UserRole.NOTARY, "13800000003");
        createUser("notary2", "公证员赵六", UserRole.NOTARY, "13800000004");
        createUser("reviewer1", "审核员孙七", UserRole.REVIEWER, "13800000005");
        createUser("archivist1", "档案员周八", UserRole.ARCHIVIST, "13800000006");
        createUser("cashier1", "收费员吴九", UserRole.CASHIER, "13800000007");
    }

    private void createUser(String username, String realName, UserRole role, String phone) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(role);
        user.setRealName(realName);
        user.setPhone(phone);
        user.setEnabled(true);
        userRepository.save(user);
    }

    private void createSampleCases() {
        User applicant1 = userRepository.findByUsername("applicant1").orElseThrow();
        User applicant2 = userRepository.findByUsername("applicant2").orElseThrow();
        User notary1 = userRepository.findByUsername("notary1").orElseThrow();
        User notary2 = userRepository.findByUsername("notary2").orElseThrow();
        User reviewer1 = userRepository.findByUsername("reviewer1").orElseThrow();

        WillCase case1 = new WillCase();
        case1.setCaseNumber("WC20240601001");
        case1.setStatus(CaseStatus.UNDER_REVIEW);
        case1.setApplicantId(applicant1.getId());
        case1.setNotaryId(notary1.getId());
        case1.setReviewerId(reviewer1.getId());
        willCaseRepository.save(case1);

        WillCase case2 = new WillCase();
        case2.setCaseNumber("WC20240602001");
        case2.setStatus(CaseStatus.DRAFT);
        case2.setApplicantId(applicant2.getId());
        case2.setNotaryId(notary2.getId());
        willCaseRepository.save(case2);

        WillCase case3 = new WillCase();
        case3.setCaseNumber("WC20240603001");
        case3.setStatus(CaseStatus.WITNESS_SIGNING);
        case3.setApplicantId(applicant1.getId());
        case3.setNotaryId(notary1.getId());
        case3.setReviewerId(reviewer1.getId());
        willCaseRepository.save(case3);
    }

    private void createSampleData() {
        WillCase case1 = willCaseRepository.findByCaseNumber("WC20240601001").orElseThrow();
        WillCase case3 = willCaseRepository.findByCaseNumber("WC20240603001").orElseThrow();

        IdentityInfo identity1 = new IdentityInfo();
        identity1.setCaseId(case1.getId());
        identity1.setName("张三");
        identity1.setIdType("身份证");
        identity1.setIdNumber("110101199001011234");
        identity1.setGender("男");
        identity1.setBirthDate(LocalDate.of(1990, 1, 1));
        identity1.setNationality("中国");
        identity1.setAddress("北京市朝阳区某某路1号");
        identity1.setPhone("13800000001");
        identityInfoRepository.save(identity1);

        PropertyInventory prop1 = new PropertyInventory();
        prop1.setCaseId(case1.getId());
        prop1.setType(PropertyType.REAL_ESTATE);
        prop1.setDescription("北京市朝阳区三居室一套");
        prop1.setValue(new BigDecimal("5000000"));
        prop1.setAddress("北京市朝阳区某某路1号");
        prop1.setArea(new BigDecimal("120.5"));
        prop1.setCertificateNo("京房权证朝字第123456号");
        propertyInventoryRepository.save(prop1);

        PropertyInventory prop2 = new PropertyInventory();
        prop2.setCaseId(case1.getId());
        prop2.setType(PropertyType.DEPOSIT);
        prop2.setDescription("中国银行定期存款");
        prop2.setValue(new BigDecimal("1000000"));
        prop2.setBank("中国银行北京朝阳支行");
        prop2.setAccountNo("6227000012345678901");
        propertyInventoryRepository.save(prop2);

        KinshipRelation kin1 = new KinshipRelation();
        kin1.setCaseId(case1.getId());
        kin1.setRelativeName("张四");
        kin1.setRelation("儿子");
        kin1.setIdNumber("110101201501011234");
        kin1.setPhone("13800000010");
        kin1.setIsBeneficiary(true);
        kinshipRelationRepository.save(kin1);

        HealthDeclaration health1 = new HealthDeclaration();
        health1.setCaseId(case1.getId());
        health1.setMentalStatus("正常");
        health1.setChronicDiseases("无");
        health1.setMedicationInfo("无");
        health1.setDeclarationDate(LocalDate.of(2024, 6, 1));
        health1.setDeclaredBy("张三");
        healthDeclarationRepository.save(health1);

        User notary1 = userRepository.findByUsername("notary1").orElseThrow();

        AppointmentSlot slot1 = new AppointmentSlot();
        slot1.setNotaryId(notary1.getId());
        slot1.setDate(LocalDate.of(2024, 6, 15));
        slot1.setStartTime(LocalTime.of(9, 0));
        slot1.setEndTime(LocalTime.of(10, 0));
        slot1.setStatus(SlotStatus.AVAILABLE);
        appointmentSlotRepository.save(slot1);

        AppointmentSlot slot2 = new AppointmentSlot();
        slot2.setNotaryId(notary1.getId());
        slot2.setDate(LocalDate.of(2024, 6, 15));
        slot2.setStartTime(LocalTime.of(10, 0));
        slot2.setEndTime(LocalTime.of(11, 0));
        slot2.setStatus(SlotStatus.BOOKED);
        slot2.setCaseId(case1.getId());
        appointmentSlotRepository.save(slot2);

        InterviewRecord interview1 = new InterviewRecord();
        interview1.setCaseId(case3.getId());
        interview1.setNotaryId(notary1.getId());
        interview1.setInterviewDate(LocalDate.of(2024, 6, 10));
        interview1.setContent("申请人精神状态良好，遗嘱意愿明确");
        interview1.setRiskAlert(false);
        interview1.setSignedByApplicant(true);
        interviewRecordRepository.save(interview1);

        MaterialItem mat1 = new MaterialItem();
        mat1.setCaseId(case1.getId());
        mat1.setType("身份证明");
        mat1.setName("身份证复印件");
        mat1.setFileUrl("/uploads/id_card.pdf");
        mat1.setStatus(MaterialStatus.APPROVED);
        materialItemRepository.save(mat1);

        MaterialItem mat2 = new MaterialItem();
        mat2.setCaseId(case1.getId());
        mat2.setType("财产证明");
        mat2.setName("房产证复印件");
        mat2.setFileUrl("/uploads/house_cert.pdf");
        mat2.setStatus(MaterialStatus.PENDING);
        materialItemRepository.save(mat2);

        Beneficiary ben1 = new Beneficiary();
        ben1.setCaseId(case1.getId());
        ben1.setName("张四");
        ben1.setRelation("儿子");
        ben1.setIdNumber("110101201501011234");
        ben1.setPhone("13800000010");
        ben1.setShare(new BigDecimal("1.0"));
        beneficiaryRepository.save(ben1);

        Witness wit1 = new Witness();
        wit1.setCaseId(case3.getId());
        wit1.setName("证人甲");
        wit1.setIdType("身份证");
        wit1.setIdNumber("110101198001011234");
        wit1.setPhone("13800000020");
        wit1.setRelation("邻居");
        wit1.setDeclaredNoConflict(true);
        witnessRepository.save(wit1);

        FeeRecord fee1 = new FeeRecord();
        fee1.setCaseId(case1.getId());
        fee1.setFeeType("公证费");
        fee1.setAmount(new BigDecimal("500"));
        fee1.setStatus(FeeStatus.PAID);
        fee1.setPaidBy(case1.getApplicantId());
        fee1.setReceiptNumber("RCP20240601001");
        feeRecordRepository.save(fee1);

        FeeRecord fee2 = new FeeRecord();
        fee2.setCaseId(case1.getId());
        fee2.setFeeType("档案保管费");
        fee2.setAmount(new BigDecimal("100"));
        fee2.setStatus(FeeStatus.UNPAID);
        feeRecordRepository.save(fee2);

        User archivist1 = userRepository.findByUsername("archivist1").orElseThrow();

        ArchiveRecord archive1 = new ArchiveRecord();
        archive1.setCaseId(case3.getId());
        archive1.setSealedBy(archivist1.getId());
        archive1.setPhysicalLocation("A区-3号柜-12号");
        archive1.setElectronicPath("/archive/WC20240603001");
        archive1.setStatus(ArchiveStatus.SEALED);
        archiveRecordRepository.save(archive1);
    }
}
