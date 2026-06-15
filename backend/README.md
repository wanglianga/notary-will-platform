# 公证遗嘱预约平台后端

## 原始需求

> Build a complete Spring Boot 3 backend for a notary will appointment platform at d:\code\solocoder-0608-wl\wl-322\backend. Use Maven, Java 17, Spring Data JPA, H2 database, Spring Security. Create all necessary files.
>
> Package structure: com.notary.will
>
> Entities to create (all with id, createdAt, updatedAt timestamps):
> 1. User, 2. WillCase, 3. IdentityInfo, 4. PropertyInventory, 5. KinshipRelation, 6. HealthDeclaration, 7. AppointmentSlot, 8. InterviewRecord, 9. WitnessVideo, 10. MaterialItem, 11. ArchiveRecord, 12. Beneficiary, 13. Witness, 14. SupplementItem, 15. FeeRecord

## 技术栈

- Java 17
- Spring Boot 3.2.5
- Spring Data JPA
- Spring Security + JWT
- H2 Database (文件模式)
- Lombok
- Maven

## 项目结构

```
backend/
├── src/main/java/com/notary/will/
│   ├── NotaryWillApplication.java
│   ├── config/          # 安全、CORS、JPA审计、MVC配置
│   ├── controller/      # 12个REST控制器
│   ├── dto/             # 请求/响应DTO
│   ├── entity/          # 15个JPA实体 + BaseEntity
│   ├── enums/           # 8个枚举类型
│   ├── exception/       # 全局异常处理
│   ├── init/            # 数据初始化器
│   ├── repository/      # 15个JPA Repository
│   ├── security/        # JWT认证过滤器
│   └── service/         # 12个业务服务类
├── src/main/resources/
│   └── application.yml
├── Dockerfile
├── .dockerignore
└── pom.xml
```

## API 接口

| 路径 | 方法 | 说明 | 权限 |
|------|------|------|------|
| /api/auth/login | POST | 登录 | 公开 |
| /api/auth/register | POST | 注册 | 公开 |
| /api/cases | GET/POST | 案件列表/创建 | NOTARY/REVIEWER等 |
| /api/cases/{id} | GET/PUT/DELETE | 案件详情/更新/删除 | 角色限控 |
| /api/cases/{id}/status | PATCH | 状态流转 | NOTARY/REVIEWER/ARCHIVIST |
| /api/cases/{caseId}/identity | GET/POST | 身份信息 | APPLICANT/NOTARY/REVIEWER |
| /api/cases/{caseId}/properties | GET/POST | 财产清单 | 多角色 |
| /api/cases/{caseId}/kinships | GET/POST | 亲属关系 | 多角色 |
| /api/cases/{caseId}/health | GET/POST | 健康声明 | APPLICANT/NOTARY/REVIEWER |
| /api/appointments | GET/POST | 预约管理 | 多角色 |
| /api/cases/{caseId}/interviews | GET/POST | 面谈记录 | NOTARY/REVIEWER |
| /api/cases/{caseId}/videos | GET/POST | 见证视频 | NOTARY/REVIEWER/ARCHIVIST |
| /api/cases/{caseId}/materials | GET/POST | 材料审核 | 多角色 |
| /api/cases/{caseId}/archive | GET/POST | 归档封存 | ARCHIVIST |
| /api/cases/{caseId}/fees | GET/POST | 费用管理 | 多角色 |

## 测试用户

| 用户名 | 密码 | 角色 |
|--------|------|------|
| applicant1 | password123 | 申请人 |
| applicant2 | password123 | 申请人 |
| notary1 | password123 | 公证员 |
| notary2 | password123 | 公证员 |
| reviewer1 | password123 | 审核员 |
| archivist1 | password123 | 档案员 |
| cashier1 | password123 | 收费员 |

## 启动方式

### 前置要求

- Java 17+
- Maven 3.8+（或使用项目内置 mvnw）
- Docker & Docker Compose（可选）

### Docker 一键启动（优先）

```bash
docker compose up --build
```

后台运行：

```bash
docker compose up --build -d
```

停止服务：

```bash
docker compose down
```

访问地址：http://localhost:8080

### 本地启动

#### 1. 安装依赖

```bash
cd backend
mvn clean install -DskipTests
```

#### 2. 启动服务

```bash
mvn spring-boot:run
```

访问地址：http://localhost:8080

H2 控制台：http://localhost:8080/h2-console（JDBC URL: jdbc:h2:file:./data/notary-will;AUTO_SERVER=TRUE，用户名: sa，密码为空）

### 隐私保护

- 申请人只能访问自己的案件
- 健康声明、面谈记录、见证视频仅限 NOTARY/REVIEWER/ARCHIVIST 角色（案件所有者申请人除外）访问
- 家属成员（非 APPLICANT 案件所有者）无法查看敏感内容
