# 👥 2026-03-sb10-part2-team5

## 📈 금융 지수 데이터를 한눈에 제공하는 대시보드 서비스 [ Findex ]

<table align="center">
    <tr align="center">
        <td colspan="5">
            <p style="font-size: x-large; font-weight: bold;">2026년 3월 Spring Backend10기 Team5</p>
        </td>
    </tr>
    <tr align="center">
        <td style="min-width: 150px;">
            <a href="https://github.com/dstle">
                <img src="https://avatars.githubusercontent.com/u/76402694?s=70&v=4" width="200" alt="송시연_깃허브프로필" />
                <br />
                <b>dstle</b>
            </a>
        </td>
        <td style="min-width: 150px;">
            <a href="https://github.com/Minbro-Kim">
                <img src="https://avatars.githubusercontent.com/u/144206885?v=4" width="200" alt="김민형_깃허브프로필">
                <br />
                <b>Minbro-Kim</b>
            </a>
        </td>
        <td style="min-width: 150px;">
            <a href="https://github.com/PSG-00">
                <img src="https://avatars.githubusercontent.com/u/51737908?s=70&v=4" width="200" alt="박성국_깃허브프로필">
                <br />
                <b>PSG-00</b>
            </a>
        </td>
        <td style="min-width: 150px;">
            <a href="https://github.com/jh9dev">
                <img src="https://avatars.githubusercontent.com/u/151911592?s=96&v=4" width="200" alt="성주현_깃허브프로필">
                <br />
                <b>jh9dev</b>
            </a>
        </td>
        <td style="min-width: 150px;">
            <a href="https://github.com/JunCH163">
                <img src="https://avatars.githubusercontent.com/u/122216981?s=70&v=4" width="200" alt="전창현_깃허브프로필">
                <br />
                <b>JunCH163</b>
            </a>
        </td>
    </tr>
    <tr align="center">
        <td>
            <b>송시연</b>
        </td>
        <td>
            <b>김민형</b>
        </td>
        <td>
            <b>박성국</b>
        </td>
        <td>
            <b>성주현</b>
        </td>
        <td>
            <b>전창현</b>
        </td>
    </tr>
    <tr align="center">
        <td>
            <b>Team Leader</b>, Backend
        </td>
        <td>
            Backend
        </td>
        <td>
            Backend
        </td>
        <td>
            Backend
        </td>
        <td>
            Backend
        </td>
    </tr>
</table>




## 📈 프로젝트 소개

- 금융 지수 데이터 대시보드 서비스의 Spring 백엔드 시스템 구축
- 프로젝트 기간: 2026.03.11 ~ 2026.03.20
- 주요 기능
  - 지수 정보 관리
    - 지수 정보를 사용자가 등록 및 수정, 삭제할 수 있습니다.
    - 지수 정보 목록을 `지수 분류명`과 `지수명`, `즐겨찾기`로 필터링하여 조회할 수 있습니다.
    - 지수 정보 목록을 `지수 분류명`과  `지수명`, `채용 종목 수`로 정렬하여 조회할 수 있습니다.
  - 지수 데이터 관리
    - 지수 데이터를 사용자가 등록, 수정, 삭제할 수 있습니다.
    - 지수 데이터 목록을 `지수`와 `날짜`로 필터링하여 조회할 수 있습니다.
    - 지수 데이터 목록을 `소스타입`을 제외한 모든 속성으로 정렬하여 조회할 수 있습니다.
    - 특정 지수 정보의 특정 기간동안의 지수 데이터 목록을 CSV파일로 다운받을 수 있습니다. 
  - 연동 작업 관리
    - `금융위원회_지수시세정보 오픈 API`로 지수 정보와 지수 데이터를 등록, 수정하고, 연동 작업을 저장합니다.
    - 연동 작업 목록을 `지수 타입(지수 정보/지수 데이터)`와 `날짜`, `지수명`, `성공여부`로 필터링하여 조회할 수 있습니다.
    - 연동 작업 목록을 `대상 날짜`와 `작업일시`로 정렬하여 조회할 수 있습니다.
  - 자동 연동 설정 관리
    - 자동 연동 설정을 등록, 수정할 수 있습니다.
    - 자동 연동 설정 목록을 `지수`, `활성화`로 정렬하여 조회할 수 있습니다.
    - 지수 데이터 연동 프로세스를 주기마다 반복하여 저장합니다.
  - 대시보드 관리
    - 즐겨찾기된 지수의 성과 정보(종가 기준)를 포함하여 주요 지수 현황을 요약하여 확인할 수 있습니다.
    - 월/분기/년 단위로 종가 기준의 지수 차트를 확인할 수 있습니다.
    - 전일/전주/전월 대비 종가 기준의 지수 성과 랭킹을 `지수 정보`로 필터링하여 확인할 수 있습니다.
- 주요 화면


## 🛠️ **기술 스택**

### Backend
<p>
    <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white"/>
    <img src="https://img.shields.io/badge/Spring Data JPA-6DB33F?style=flat-square&logoColor=white"/>
    <img src="https://img.shields.io/badge/RestClient-6DB33F?style=flat-square&logo=spring&logoColor=white"/>
    <img src="https://img.shields.io/badge/MapStruct-6DB33F?style=flat-square&logo=spring&logoColor=white"/>
    <img src="https://img.shields.io/badge/QueryDSL-0769AD?style=flat-square&logo=hibernate&logoColor=white"/>
    <img src="https://img.shields.io/badge/Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=black"/>
    <img src="https://img.shields.io/badge/JUnit5-25A162?style=flat-square&logo=junit5&logoColor=white"/>
</p>

### Database & Infra
<p>
    <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white"/>
    <img src="https://img.shields.io/badge/H2-09476B?style=flat-square&logo=h2database&logoColor=white"/>
    <img src="https://img.shields.io/badge/Railway-0B0D0E?style=flat-square&logo=railway&logoColor=white"/>
</p>

### Collaboration
<p>
    <img src="https://img.shields.io/badge/Git-F05032?style=flat-square&logo=Git&logoColor=white"/>
    <img src="https://img.shields.io/badge/Github-181717?style=flat-square&logo=github&logoColor=white"/>
    <img src="https://img.shields.io/badge/Discord-5865F2?style=flat-square&logo=discord&logoColor=white"/>
    <img src="https://img.shields.io/badge/Notion-000000?style=flat-square&logo=notion&logoColor=white"/>
    <img src="https://img.shields.io/badge/Google Sheets-34A853?style=flat-square&logo=googlesheets&logoColor=white"/>
    <img src="https://img.shields.io/badge/DBDiagram-2060cf?style=flat-square&logo=diagrams.net&logoColor=white"/>
  </p>


## 🔗 프로젝트 문서 및 링크

| Category | Link | Description |
| :--- | :--- | :--- |
| **Workspace** | [Notion Workspace](https://www.notion.so/SB-10-Part-2-5-dc847b8a4b9982febcb701a6f526f9ef) | 기획 문서, 회의록 및 팀 컨벤션 관리 |
| **Management** | [GitHub Issues & Gantt](https://github.com/orgs/sb10-part2-team5/projects/3/views/4) | 이슈 카드를 활용한 역할 및 일정 관리 |
| **요구사항 명세서** | [요구사항 명세서](https://docs.google.com/spreadsheets/d/1PLhsk7xDISDc0zdj5JRs6A1bDkwGXaO9h_cD0xURmUk/edit?gid=0#gid=0)| 구글 스프레드 시트를 활용한 요구사항 명세 작성 |
| **API Docs** | [Swagger UI](링크주소) | RESTful API 명세서 |
| **Design** | [Database ERD](https://dbdiagram.io/d/69b1119c77d079431b56b7c9) | 데이터베이스 구조 설계도 |
| **발표 자료** | [발표 자료](링크주소)/[pdf 파일]()| 발표 자료 |

-----


## 🧑‍💻 **팀원별 구현 기능 상세**

<details>
  <summary><b>🏃 송시연 (Team Leader)</b></summary>
  <div markdown="1">
    
      (자신이 개발한 기능에 대한 사진이나 gif 파일 첨부)마크다운 문법 그대로 사용해주세요
## api 구현
- **소셜 로그인 API**
    - Google OAuth 2.0을 활용한 소셜 로그인 기능 구현
    - 로그인 후 추가 정보 입력을 위한 RESTful API 엔드포인트 개발
- **회원 추가 정보 입력 API**
    - 회원 유형(관리자, 학생)에 따른 조건부 입력 처리 API 구현
      <img width="1438" height="809" alt="image" src="https://github.com/user-attachments/assets/629db1bc-1e84-435c-b60f-36e9238fcafb" />

  </div>
</details>

<details>
  <summary><b>🏃 김민형</b></summary>
  <div markdown="1">
    
      (자신이 개발한 기능에 대한 사진이나 gif 파일 첨부)마크다운 문법 그대로 사용해주세요
## api 구현
- **소셜 로그인 API**
    - Google OAuth 2.0을 활용한 소셜 로그인 기능 구현
    - 로그인 후 추가 정보 입력을 위한 RESTful API 엔드포인트 개발
- **회원 추가 정보 입력 API**
    - 회원 유형(관리자, 학생)에 따른 조건부 입력 처리 API 구현
      <img width="1438" height="809" alt="image" src="https://github.com/user-attachments/assets/629db1bc-1e84-435c-b60f-36e9238fcafb" />

  </div>
</details>

<details>
  <summary><b>🏃 박성국</b></summary>
  <div markdown="1">
    
      (자신이 개발한 기능에 대한 사진이나 gif 파일 첨부)마크다운 문법 그대로 사용해주세요
## api 구현
- **소셜 로그인 API**
    - Google OAuth 2.0을 활용한 소셜 로그인 기능 구현
    - 로그인 후 추가 정보 입력을 위한 RESTful API 엔드포인트 개발
- **회원 추가 정보 입력 API**
    - 회원 유형(관리자, 학생)에 따른 조건부 입력 처리 API 구현
      <img width="1438" height="809" alt="image" src="https://github.com/user-attachments/assets/629db1bc-1e84-435c-b60f-36e9238fcafb" />

  </div>
</details>

<details>
  <summary><b>🏃 성주현</b></summary>
  <div markdown="1">
    
      (자신이 개발한 기능에 대한 사진이나 gif 파일 첨부)마크다운 문법 그대로 사용해주세요
## api 구현
- **소셜 로그인 API**
    - Google OAuth 2.0을 활용한 소셜 로그인 기능 구현
    - 로그인 후 추가 정보 입력을 위한 RESTful API 엔드포인트 개발
- **회원 추가 정보 입력 API**
    - 회원 유형(관리자, 학생)에 따른 조건부 입력 처리 API 구현
      <img width="1438" height="809" alt="image" src="https://github.com/user-attachments/assets/629db1bc-1e84-435c-b60f-36e9238fcafb" />

  </div>
</details>

<details>
  <summary><b>🏃 전창현</b></summary>
  <div markdown="1">
    
      (자신이 개발한 기능에 대한 사진이나 gif 파일 첨부)마크다운 문법 그대로 사용해주세요
## api 구현
- **소셜 로그인 API**
    - Google OAuth 2.0을 활용한 소셜 로그인 기능 구현
    - 로그인 후 추가 정보 입력을 위한 RESTful API 엔드포인트 개발
- **회원 추가 정보 입력 API**
    - 회원 유형(관리자, 학생)에 따른 조건부 입력 처리 API 구현
      <img width="1438" height="809" alt="image" src="https://github.com/user-attachments/assets/629db1bc-1e84-435c-b60f-36e9238fcafb" />

  </div>
</details>


## 📁 **파일 구조**
- (최종 구조 확정시, 변경 예정)

<details>
  <summary>파일 구조</summary>
  <div markdown="1">
    
```text
src/
├── main/
│   ├── java/
│   │   ├── com/
│   │       ├── sprint/
│   │           ├── findex/
│   │               ├── client/
│   │               │   └── MarketIndexApiClient.java
│   │               ├── config/
│   │               │   ├── MarketIndexApiConfig.java
│   │               │   ├── MarketIndexApiProperties.java
│   │               │   ├── OpenApiConfig.java
│   │               │   └── QueryDSLConfig.java
│   │               ├── controller/
│   │               │   ├── AutoSyncConfigController.java
│   │               │   ├── DashboardController.java
│   │               │   ├── IndexDataController.java
│   │               │   ├── IndexInfoController.java
│   │               │   └── IndexSyncController.java
│   │               ├── dto/
│   │               │   ├── autosyncconfig/
│   │               │   │   ├── AutoSyncConfigDto.java
│   │               │   │   └── AutoSyncConfigUpdateRequest.java
│   │               │   ├── dashboard/
│   │               │   │   └── IndexPerformanceDto.java
│   │               │   ├── indexdata/
│   │               │   │   ├── IndexDataCreateRequest.java
│   │               │   │   ├── IndexDataDto.java
│   │               │   │   └── IndexDataUpdateRequest.java
│   │               │   ├── indexinfo/
│   │               │   │   ├── CursorPageResponseIndexInfoDto.java
│   │               │   │   ├── IndexInfoCreateRequest.java
│   │               │   │   ├── IndexInfoDto.java
│   │               │   │   ├── IndexInfoQueryCondition.java
│   │               │   │   ├── IndexInfoSummaryDto.java
│   │               │   │   └── IndexInfoUpdateRequest.java
│   │               │   ├── openapi/
│   │               │   │   ├── MarketIndexApiRequest.java
│   │               │   │   └── MarketIndexApiResponse.java
│   │               │   ├── sync/
│   │               │       ├── IndexInfoSyncSource.java
│   │               │       └── SyncJobDto.java
│   │               ├── entity/
│   │               │   ├── base/
│   │               │   │   ├── BaseEntity.java
│   │               │   │   └── BaseUpdatableEntity.java
│   │               │   ├── AutoSyncConfig.java
│   │               │   ├── IndexData.java
│   │               │   ├── IndexInfo.java
│   │               │   └── IntegrationTask.java
│   │               ├── enums/
│   │               │   ├── JobResult.java
│   │               │   ├── JobType.java
│   │               │   ├── PeriodType.java
│   │               │   └── SourceType.java
│   │               ├── exception/
│   │               │   ├── BusinessLogicException.java
│   │               │   ├── ErrorResponse.java
│   │               │   ├── ExceptionCode.java
│   │               │   ├── GlobalExceptionHandler.java
│   │               │   └── testController.java
│   │               ├── mapper/
│   │               │   ├── AutoSyncConfigMapper.java
│   │               │   ├── DashboardMapper.java
│   │               │   ├── IndexDataMapper.java
│   │               │   ├── IndexInfoMapper.java
│   │               │   ├── MarketIndexApiSyncMapper.java
│   │               │   └── SyncJobMapper.java
│   │               ├── repository/
│   │               │   ├── dsl/
│   │               │   │   ├── impl/
│   │               │   │   │   └── IndexInfoCustomRepositoryImpl.java
│   │               │   │   └── IndexInfoCustomRepository.java
│   │               │   ├── AutoSyncConfigRepository.java
│   │               │   ├── DashboardRepository.java
│   │               │   ├── IndexDataRepository.java
│   │               │   ├── IndexInfoRepository.java
│   │               │   └── IntegrationTaskRepository.java
│   │               ├── service/
│   │               │   ├── AutoSyncConfigService.java
│   │               │   ├── DashboardService.java
│   │               │   ├── IndexDataService.java
│   │               │   ├── IndexInfoService.java
│   │               │   └── IndexSyncService.java
│   │               ├── util/
│   │               │   ├── ClientIpResolver.java
│   │               │   └── SortUtils.java
│   │               └── FindexApplication.java
│   ├── resources/
│       ├── static/
│       │   ├── assets/
│       │   │   ├── fonts/
│       │   │   ├── index-Bweg6EuF.css
│       │   │   └── index-D-ZKSpBz.js
│       │   ├── favico.ico
│       │   └── index.html
│       ├── application-local.yaml
│       ├── application-prod.yaml
│       ├── application.yaml
│       ├── schema-h2.sql
│       └── schema-postgresql.sql
├── test/
    ├── java/
    │   ├── com/
    │       ├── sprint/
    │           ├── findex/
    │               └── FindexApplicationTests.java
    ├── resources/
        └── application-test.yaml
```

  </div>
</details>

## **구현 홈페이지**
<a href="https://sb10-findex-team5-production.up.railway.app/" target="_blank">
  <img src="https://github.com/user-attachments/assets/7bab84c6-9c4c-483f-b9af-ac6282a2a60e" width="600px" alt="Findex Dashboard Preview" />
</a>

> **이미지를 클릭**하면 서비스 페이지로 연결됩니다.


---

## **프로젝트 회고록**

- [송시연]()
- [김민형]()
- [박성국]()
- [성주현]()
- [전창현]()
