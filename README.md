# 公证处遗嘱预约与见证材料平台

## 项目简介

本平台是一个面向公证处的遗嘱预约与见证材料管理系统，支持申请人、公证员、材料审核员、档案室和收费窗口等多种角色协同工作，实现遗嘱公证业务的全流程数字化管理。

平台严格管理隐私、证据、谈话过程和材料版本，确保办理人员能够判断遗嘱是否真实自愿，同时防止家属越权查看敏感内容。

## 原始需求

> 请实现公证处遗嘱预约与见证材料平台，Vue3 页面给申请人、公证员、材料审核员、档案室和收费窗口使用，Spring Boot 保存身份信息、财产清单、亲属关系、身体状况声明、预约时段、谈话笔录、见证录像和归档状态。申请人提交房产、存款、股权、受益人和见证人资料；材料审核员检查证件、证明有效期和补件清单；公证员安排单独谈话、风险提示和签署见证；档案室封存纸质与电子材料。这个产品要把隐私、证据、谈话过程和材料版本管住，让办理人员能判断遗嘱是否真实自愿，家属不能越权查看敏感内容。
>
> 修复前后端接口契约：为审核、公证、档案、收费页面统一后端路由或调整前端 API，并补齐各角色真实页面操作验证；同时为财产、亲属、受益人、见证人、材料等案件子资源增加申请人归属校验和越权写入/读取测试，确保同角色申请人不能访问或修改他人敏感材料。

## 技术栈

### 后端
- Spring Boot 3.2.5
- Spring Security + JWT
- Spring Data JPA
- H2 Database (内嵌数据库)
- Lombok

### 前端
- Vue 3.5
- TypeScript
- Element Plus 2.x
- Pinia (状态管理)
- Vue Router 5
- Axios
- Vite

## 系统角色

| 角色 | 说明 | 主要功能 |
|------|------|----------|
| **申请人** | 遗嘱订立人 | 提交身份信息、财产清单、亲属关系、健康声明、受益人、见证人信息，预约办理时间，上传材料 |
| **材料审核员** | 负责材料审核 | 检查证件有效期、证明材料、发出补件通知 |
| **公证员** | 负责公证办理 | 安排预约、单独谈话、风险提示、签署见证、制作谈话笔录 |
| **档案室** | 负责档案管理 | 封存纸质与电子材料、档案查询、档案借阅管理 |
| **收费窗口** | 负责费用管理 | 费用核算、收费登记、票据管理 |

## 核心功能模块

### 1. 身份信息管理
- 申请人基本信息采集
- 证件类型和号码管理
- 隐私数据加密存储

### 2. 财产清单管理
- **房产**：地址、面积、产权证号
- **存款**：银行、账号、金额
- **股权**：公司名称、持股比例
- **其他财产**：描述、价值
- 财产信息版本追踪

### 3. 亲属关系管理
- 直系亲属信息录入
- 受益人标识
- 利益冲突排查

### 4. 健康状况声明
- 精神状态声明
- 慢性疾病说明
- 用药情况记录
- 声明日期和签署

### 5. 预约管理
- 公证员时段设置
- 申请人在线预约
- 预约确认与取消

### 6. 谈话笔录
- 单独谈话记录
- 风险提示记录
- 申请人签字确认
- 谈话过程录像管理

### 7. 见证管理
- 见证人信息管理
- 无利害关系声明
- 见证签署过程

### 8. 材料审核
- 证件有效期检查
- 证明材料审核
- 补件清单管理
- 审核意见记录

### 9. 归档管理
- 纸质材料封存
- 电子材料归档
- 档案位置管理
- 封存状态追踪

### 10. 费用管理
- 公证费用核算
- 收费记录
- 票据管理

## 安全特性

### 核心安全机制

1. **基于角色的访问控制 (RBAC)**：不同角色只能访问授权范围内的功能和数据
2. **JWT 认证**：无状态身份认证，支持令牌过期和刷新
3. **隐私数据保护**：敏感信息加密存储，家属无法越权查看
4. **操作留痕**：所有关键操作都有审计记录
5. **版本控制**：财产清单、健康声明等支持版本历史追踪
6. **CORS 安全配置**：严格的跨域访问控制

### 新增：案件子资源归属校验（第二阶段）

7. **统一权限校验基类**：`BaseCaseController` 提供公共的 `checkCaseOwnership()` 方法
8. **申请人归属校验**：所有案件子资源接口都进行归属校验，确保申请人只能访问自己的案件数据
9. **覆盖的子资源**：
   - 身份信息 (`/api/cases/{caseId}/identity`)
   - 财产清单 (`/api/cases/{caseId}/properties`)
   - 亲属关系 (`/api/cases/{caseId}/kinship`)
   - 健康声明 (`/api/cases/{caseId}/health`)
   - 受益人 (`/api/cases/{caseId}/beneficiaries`)
   - 见证人 (`/api/cases/{caseId}/witnesses`)
   - 材料审核 (`/api/cases/{caseId}/materials`)
   - 谈话笔录 (`/api/cases/{caseId}/interview`)
   - 归档记录 (`/api/cases/{caseId}/archive`)
   - 费用记录 (`/api/cases/{caseId}/fees`)
   - 见证录像 (`/api/cases/{caseId}/video`)
10. **角色权限规则**：
    - 申请人 (APPLICANT)：只能访问 `applicantId` 等于当前用户ID的案件
    - 公证员 (NOTARY)、审核员 (REVIEWER)、档案员 (ARCHIVIST)、收费员 (CASHIER)：可访问所有案件

### 越权访问测试验证

已通过 11 个集成测试用例验证权限控制有效性：

- ✅ 申请人可以访问自己的案件财产信息
- ✅ 申请人不能访问他人的案件财产信息
- ✅ 申请人不能修改他人的案件财产信息
- ✅ 申请人不能为他人案件添加财产
- ✅ 材料审核员可以访问所有案件的财产信息
- ✅ 申请人不能访问他人的身份信息
- ✅ 申请人不能访问他人的亲属关系
- ✅ 申请人不能访问他人的受益人信息
- ✅ 申请人不能访问他人的见证人信息
- ✅ 申请人不能访问他人的材料信息
- ✅ 未登录用户不能访问任何案件数据

### 统一接口契约（第二阶段）

为各角色创建专用 Controller，统一路由前缀：

| 角色 | 路由前缀 | Controller |
|------|----------|------------|
| 审核员 | `/api/review/*` | `ReviewController` |
| 公证员 | `/api/notary/*` | `NotaryController` |
| 档案员 | `/api/archive/*` | `ArchiveController` |
| 收费员 | `/api/cashier/*` | `CashierController` |

所有路由与前端 API 调用完全匹配，确保前后端接口契约一致。

## 启动方式

### 前置要求

- Docker 20.10+
- Docker Compose v2+
- 或本地开发环境：
  - JDK 17+
  - Node.js 18+
  - Maven 3.8+

---

### 方式一：Docker 一键启动（推荐）

#### 1. 启动服务

在项目根目录执行：

```bash
docker compose up --build
```

如需后台运行：

```bash
docker compose up --build -d
```

#### 2. 访问地址

- 前端页面：http://localhost
- 后端API：http://localhost:8080
- H2数据库控制台：http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:file:/app/data/notary-will;AUTO_SERVER=TRUE`
  - 用户名: `sa`
  - 密码: （空）

#### 3. 停止服务

```bash
docker compose down
```

如需清理数据卷：

```bash
docker compose down -v
```

#### 4. 配置验证

```bash
docker compose config
```

查看服务日志：

```bash
docker compose logs -f
```

---

### 方式二：本地开发启动

#### 1. 启动后端

```bash
cd backend
mvn clean package -DskipTests
java -jar target/will-platform-1.0.0.jar
```

后端服务启动在 http://localhost:8080

#### 2. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端服务启动在 http://localhost:5173

---

## 默认测试账号

系统启动后会自动创建以下测试用户，密码均为 `password123`：

| 用户名 | 角色 | 姓名 |
|--------|------|------|
| applicant1 | 申请人 | 张三 |
| applicant2 | 申请人 | 李四 |
| notary1 | 公证员 | 王五 |
| notary2 | 公证员 | 赵六 |
| reviewer1 | 审核员 | 孙七 |
| archivist1 | 档案员 | 周八 |
| cashier1 | 收费员 | 吴九 |

## 项目结构

```
.
├── backend/                    # Spring Boot 后端
│   ├── src/main/java/com/notary/will/
│   │   ├── config/            # 配置类
│   │   ├── controller/        # 控制器
│   │   ├── dto/               # 数据传输对象
│   │   ├── entity/            # 实体类
│   │   ├── enums/             # 枚举类型
│   │   ├── exception/         # 异常处理
│   │   ├── init/              # 数据初始化
│   │   ├── repository/        # 数据访问层
│   │   ├── security/          # 安全认证
│   │   └── service/           # 业务逻辑层
│   ├── src/main/resources/
│   │   └── application.yml    # 应用配置
│   ├── data/                  # H2数据库文件
│   ├── uploads/               # 文件上传目录
│   ├── Dockerfile
│   ├── .dockerignore
│   └── pom.xml
├── frontend/                   # Vue3 前端
│   ├── src/
│   │   ├── api/               # API接口
│   │   ├── layouts/           # 布局组件
│   │   ├── router/            # 路由配置
│   │   ├── stores/            # 状态管理
│   │   ├── utils/             # 工具函数
│   │   ├── views/             # 页面组件
│   │   ├── App.vue
│   │   └── main.ts
│   ├── Dockerfile
│   ├── .dockerignore
│   ├── nginx.conf
│   └── package.json
├── Dockerfile                  # 根目录Dockerfile（后端构建入口）
├── .dockerignore               # 根目录忽略文件
├── docker-compose.yml          # Docker编排配置
└── README.md                   # 本文件
```

## 业务流程

### 遗嘱公证办理流程

```
申请人提交材料 → 材料审核员审核 → 审核通过 → 公证员预约安排
     ↓                ↓
  材料不齐全      审核不通过
     ↓                ↓
  补件通知        退回修改
     ↓
  重新提交 → 重新审核
                       ↓
                审核通过
                       ↓
                预约办理
                       ↓
                单独谈话 + 风险提示
                       ↓
                谈话笔录确认
                       ↓
                见证签署
                       ↓
                收费窗口缴费
                       ↓
                档案室归档封存
                       ↓
                结案
```

## API 接口说明

### 认证接口
- `POST /api/auth/login` - 用户登录
- `GET /api/auth/me` - 获取当前用户信息

### 案件管理
- `GET /api/cases` - 获取案件列表
- `GET /api/cases/my` - 获取我的案件（申请人）
- `GET /api/cases/{id}` - 获取案件详情
- `POST /api/cases` - 创建案件
- `PATCH /api/cases/{id}/status` - 状态流转

### 子资源接口
- `/api/cases/{caseId}/identity` - 身份信息
- `/api/cases/{caseId}/properties` - 财产清单
- `/api/cases/{caseId}/kinship` - 亲属关系
- `/api/cases/{caseId}/health` - 健康声明
- `/api/cases/{caseId}/beneficiaries` - 受益人
- `/api/cases/{caseId}/witnesses` - 见证人
- `/api/cases/{caseId}/appointment` - 预约
- `/api/cases/{caseId}/materials` - 材料
- `/api/cases/{caseId}/interview` - 谈话笔录
- `/api/cases/{caseId}/archive` - 归档

## 数据版本控制

- 财产清单、健康声明、谈话笔录等关键数据支持 `@Version` 乐观锁
- 修改时自动版本递增，防止并发修改冲突
- 保留完整的修改历史，支持追溯

## 隐私保护措施

1. **数据隔离**：申请人只能查看自己的案件
2. **敏感字段脱敏**：证件号码等敏感信息显示时自动脱敏
3. **访问日志**：所有敏感数据访问都有记录
4. **家属权限控制**：家属角色（如有的话）无法查看遗嘱内容等敏感信息

## 注意事项

1. 本系统使用 H2 内嵌数据库，数据存储在 `backend/data/` 目录
2. 生产环境建议替换为 MySQL 或 PostgreSQL 等数据库
3. JWT 密钥请在生产环境修改为安全的随机字符串
4. 文件上传存储在 `backend/uploads/` 目录，生产环境建议使用对象存储
5. 首次启动会自动初始化测试数据，生产环境请禁用 `DataInitializer`

## 许可证

MIT License
