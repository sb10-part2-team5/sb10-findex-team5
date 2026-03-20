# 👥 2026-03-sb10-part2-team5
## 📈 금융 지수 데이터를 한눈에 제공하는 대시보드 서비스 [ Findex ]

<table align="center">
    <tr align="center">
        <td colspan="5">
            <p style="font-size: x-large; font-weight: bold;">2026년 3월 Spring Backend10기 Ant-Man (Team5)</p>
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
<img width="399" alt="Image" src="https://github.com/user-attachments/assets/bc7e484e-4924-48bc-8009-4fb790f4aabe" />    
<img width="1026" height="100" alt="image" src="https://github.com/user-attachments/assets/b3e90db6-739c-4937-868d-327d7ea30c7c" />

## API 구현

- **자동 연동 설정 생성/수정 API**
  - AutoSyncConfig 엔티티 설계 및 생성/수정 RESTful API 구현
  - 지수 등록 시 비활성 상태의 자동 연동 설정이 함께 생성되는 연동 로직 구현
  - DTO 검증 로직 및 Swagger 컨트롤러 문서화 추가
- **자동 연동 목록 조회 API**
  - QueryDSL 기반 필터링(indexInfoId, enabled), 단일 필드 정렬, 커서 기반 페이지네이션 구현
  - `AutoSyncConfigSortField` Enum을 도입하여 정렬 대상 필드 반환, 커서 값 추출, 커서 조건 생성을 타입 안전하게 처리

## 자동 연동 스케줄러

- **자동 동기화 파이프라인**
  - 10분 간격으로 연동 대상을 식별하고 금융위원회 오픈 API를 호출하여 자동 동기화하는 스케줄러 구현
  - `AutoSyncScheduler` → `IntegrationTaskService` → `IndexSyncService` 파이프라인 설계
  - 동기화 이력이 없는 경우 `baseDateFrom = baseDateTo`로 1일치만 동기화하도록 기준일 정책 적용

## 성능 최적화

- **동적 TTL 캐시**
  - 금융위원회 오픈 API 응답에 대한 캐시 레이어(`MarketIndexApiCacheService`) 구현
  - 과거 데이터(변경 불가) 7일 TTL / 당일 데이터(갱신 가능) 1시간 TTL 차등 적용
  - 지수 데이터 연동 약 1,000건 기준 응답 시간 개선 (4183ms → 495ms)
  - 지수 정보 연동 약 200건 기준 응답 시간 개선 (892ms → 210ms)

## 프로젝트 초기 설정 및 인프라

- **개발 환경 구성**
  - H2(로컬) / PostgreSQL(운영) 프로필 분리
  - PR 템플릿, GitHub Projects 보드 설정, Google Java Style XML 적용
  - Railway 플랫폼을 통한 서비스 배포
  </div>
</details>

<details>
  <summary><b>🏃 김민형</b></summary>
  <div markdown="1">

### 1. 지수 정보 관리 API

- **지수 정보 CRUD 구현**: 지수 정보의 등록, 수정, 삭제, 전체 목록 및 요약 목록 조회 API 개발
- **QueryDSL 기반 정렬 조회**:
  - 다양한 필드에 대응하는 **동적 정렬 및 커서 기반 페이지네이션** 구현
  - 첫 번째 페이지 조회 시 불필요한 `Count` 쿼리가 발생하지 않도록 최적화하여 DB 부하 감소
- **DTO Projection 최적화**: 요약 목록 조회 시 엔티티 전체가 아닌 필요한 필드만 직접 조회하여 응답 속도 및 메모리 효율 개선

### 2. 전역 예외 처리 및 로그 시스템 (Monitoring)

- **전역 예외 처리(Global Exception Handling)**:
  - `@RestControllerAdvice`를 활용하여 비즈니스 에러와 시스템 에러를 분리 설계
  - 파라미터 유효성 검증 실패 시 사용자에게 명확한 에러 메시지를 전달하는 통합 응답 구조 구축
- **AOP 기반 성능 추적 로그**:
  - **서비스 별 임계값 설정**: 일반 요청(150ms)과 외부 연동 작업(500ms)의 성능 기준을 분리하여 정밀한 모니터링 환경 구축
  - 실패 시 파라미터 값을 100자 내로 요약 출력하여 로그 가독성 확보

### 3. 성능 최적화 및 인프라 개선 (Performance & Infra)

- **네트워크 지연 개선 (리전 최적화)**:
  - 브라우저 Waterfall 분석을 통해 지연 원인을 파악하고, 서버와 데이터베이스 리전을 **미국(US)에서 싱가포르(SG)**로 이전
  - 전체 응답 속도 약 **44% 개선 (1.17s → 0.65s)** 
- **삭제 로직 최적화 (N+1 해결)**:
  - 대량 데이터 삭제 시 발생하는 N+1 문제를 **벌크 쿼리**로 해결
  - DB 레벨의 `ON DELETE CASCADE` 설정을 활용하여 최종적으로 삭제 쿼리를 **2번에서 1번으로 단축**

<img width="1437" alt="Image" src="https://github.com/user-attachments/assets/259036d9-bf1a-4262-ae04-8fa36c58b231" />
  </div>
</details>

<details>
  <summary><b>🏃 박성국</b></summary>
  <div markdown="1">

<img width="2460" alt="Image" src="https://github.com/user-attachments/assets/0efc1622-a39d-4864-91ef-11b1ebb0cb15" />

## API구현
- **지수 데이터 관련 API 구현 AP**
  - 지수 데이터 CRUD 및 Export 구현

- **지수 데이터 입력**
  - 지수 데이터 입력을 위한 create 기능 구현
  - 지수 데이터 입력을 위한 RESTful API 엔드포인트 개발
  - 생성 메서드에서 입력값 검증을 통한 데이터 무결성 보장

- **지수 데이터 수정**
  - 지수 데이터 수정을 위한 update 기능 구현
  - 지수 데이터 수정을 위한 RESTful API 엔드포인트 개발
  - 수정 메서드에서 입력값 검증을 통한 데이터 무결성 보장

- **지수 데이터 삭제**
  - 지수 데이터 삭제를 위한 delete 기능 구현
  - 지수 데이터 삭제을 위한 RESTful API 엔드포인트 개발
  - 연관 데이터인 IndexInfo와 단방향이라서 Cascade 처리 없음, 데이터 무결성 보장

- **지수 데이터 조회**
  - 지수 데이터 조회를 위한 read 기능 구현
  - 지수 데이터 조회를 위한 RESTful API 엔드포인트 개발
  - 커서 기반 페이지네이션 적용을 통한 일관된 성능 제공
  - 여러 지수 데이터를 기준으로 정렬 기능 제공
  - 지수명, 날짜를 기반으로 필터링 기능 제공
  - QueryDSL을 통한 쿼리 최적화로 타입 안정성 제공으로 인한 가독성 향상 및 유지보수성 용이

- **지수 데이터 다운로드**
  - 지수 데이터 다운로드를 위한 export 기능 구현
  - 지수 데이터 다운로드를 위한 RESTful API 엔드포인트 개발
  - 외부 라이브러리 의존없이 스프링 기반 POJO로 구현
  - 스프링 Resource 추상화 인터페이스 사용으로 다양한 자원에 대한 확장성 확보
  - 10만건의 데이터가 약 10MB로 현재 데이터를 한번에 처리함. 추후 데이터 크기 증가에 대비해 스트리밍 방식으로 인터페이스 교체 가능
  </div>
</details>

<details>
  <summary><b>🏃 성주현</b></summary>
  <div markdown="1">

## API 구현

- **지수 정보 연동**
  - 금융위원회 Open API를 호출해 최신 지수 정보를 수집하고 DB에 반영하는 API 구현
  - `IndexSyncController`, `IndexSyncService`를 중심으로 연동 흐름 구성
  - Open API 응답을 내부 연동 DTO로 변환하여 기존 데이터 존재 여부를 판단한 뒤 생성/수정(upsert) 방식으로 반영
  - 2024년 12월 6일 이후 변경된 일부 지수명을 반영하기 위해 `IndexNameResolver`를 도입하여 과거 명칭과 현재 명칭을 동일 지수로 인식하도록 구현
  - 연동 성공/실패 결과를 작업 이력으로 저장하고 응답 DTO로 반환

- **지수 데이터 연동**
  - 금융위원회 Open API를 호출해 특정 지수의 기간별 지수 데이터를 수집하고 DB에 반영하는 API 구현
  - `IndexSyncController`, `IndexSyncService`를 중심으로 연동 흐름 구성
  - `IndexDataSyncRequest`를 통해 대상 지수 ID 목록과 연동 기간을 입력받도록 구성
  - Open API 응답을 내부 연동 DTO로 변환하여 기존 데이터 존재 여부를 판단한 뒤 생성/수정(upsert) 방식으로 반영
  - 연동 성공/실패 결과를 작업 이력으로 저장하고 응답 DTO로 반환

- **연동 작업 목록 조회**
  - `SyncJobQueryCondition` 기반으로 연동 작업 이력을 조회하는 API 구현
  - 작업 유형, 지수 이름, 날짜, 작업 일시, 처리 결과 필터링 지원
  - 정렬 필드(`SyncJobSortField`) 값과 마지막 조회 ID(`idAfter`)를 함께 사용하는 커서 기반 페이지네이션 구현
  - 정렬 필드, 정렬 방향, 페이지 크기 기본값 보정 처리

## Open API 연동

- **금융위원회 Open API 클라이언트 구현**
  - `MarketIndexApiClient`를 통해 외부 API 호출 로직 구현
  - `MarketIndexApiProperties`로 base URL, service key를 외부 설정으로 분리
  - `MarketIndexApiRequest`에서 null/blank 파라미터를 제외한 쿼리 파라미터 생성
  - 응답 코드 검증, 파싱 오류, 연결 오류, 비정상 응답에 대한 예외 처리 구현

- **응답 매핑 및 연동 모델 구성**
  - `MarketIndexApiResponse`로 Open API 응답 구조를 모델링
  - `MarketIndexApiSyncMapper`를 통해 응답 `Item`을 `IndexInfoSyncSource`, `IndexDataSyncSource`로 변환
  - Open API 응답의 날짜 문자열과 수치 문자열을 내부 연동 DTO 형식에 맞게 변환하여 매핑

## 정확성 및 안정성 보완

- **지수명 표준화 처리**
  - `IndexNameResolver`를 통해 2024년 12월 6일 이후 변경된 지수명을 표준 이름으로 정규화
  - 단건 조회 시에는 현재 이름과 과거 이름을 함께 검색 이름으로 사용해 조회하도록 구현
  - 다건 조회 시에는 기간 내 전체 데이터를 먼저 조회한 뒤, 지수명을 표준화해 연동 대상과 일치하는 데이터만 반영하도록 구현

- **성공/실패 이력 분리 저장**
  - `IndexInfoSyncService`, `IndexDataSyncService`에서 연동 성공 이력 저장
  - `IndexInfoSyncFailureService`, `IndexDataSyncFailureService`에서 연동 실패 이력 저장
  - 지수별 `REQUIRES_NEW` 트랜잭션을 적용해 일부 실패가 전체 연동 롤백으로 이어지지 않도록 구현

- **작업자 추적 기능**
  - `ClientIpResolver`를 통해 `X-Forwarded-For`, `X-Real-IP`, `RemoteAddr` 순서로 클라이언트 IP를 추출
  - 수동 연동 요청의 작업자를 추적할 수 있도록 구현
  </div>
</details>

<details>
  <summary><b>🏃 전창현</b></summary>
  <div markdown="1">

## API 구현
- **즐겨찾기 지수 성과 조회 API**
  - 즐겨찾기된 지수들의 최신 종가 데이터를 기준으로 성과 정보를 조회하는 API 구현
  - `전일/전주/전월` 기준 성과 비교를 지원하도록 기간별 기준가 계산 로직 구현
  - 응답 데이터는 `지수 정보ID`, `지수 분류명`, `지수명`, `단위 기간 대비 등락`, `단위 기간 대비 등락률`, `현재가`, `
  단위 기간 전 값`으로 구성


- **지수 성과 랭킹 조회 API**
  - 전체 지수의 최신 데이터를 기준으로 성과를 계산하고 등락률 순으로 랭킹을 조회하는 API 구현
  - `전일/전주/전월` 기준 조회를 지원하며, 특정 지수 정보 ID로 결과를 필터링할 수 있도록 구현
  - 지수 성과 정보와 순위를 함께 반환하는 응답 구조 구현


- **지수 차트 조회 API**
  - 특정 지수의 종가 이력을 기반으로 `월/분기/년` 단위 차트 데이터를 조회하는 API 구현
  - 차트 데이터와 함께 `5일 이동평균선`, `20일 이동평균선` 데이터를 제공하도록 구현
  - 지수 메타 정보와 시계열 데이터를 함께 반환하는 대시보드 전용 응답 구조 구현
  - 차트 엔드포인트는 IndexData API 하위 경로에 구성

## 주요 구현 내용

- **DashboardService 중심의 조회 책임 분리**
  - 대시보드 전용 서비스 계층과 조회용 Repository를 분리하여 관심 지수, 성과 랭킹, 차트 기능을 책임별로 구성
  - 최신 지수 데이터 조회, 즐겨찾기 지수 조회, 기간 기준 데이터 조회용 쿼리 구현


- **성과 계산 로직 구현**
  - 기간별 이전 종가를 계산하고 이를 바탕으로 `단위 기간 대비 등락`, `단위 기간 대비 등락률`, `단위 기간 전 값`을 산출
    하는 공통 로직 구현
  - 전주/전월 기준 데이터가 없을 경우 현재 데이터의 대비값을 활용하는 fallback 처리 로직 구현


- **차트 데이터 가공**
  - 최신 기준일을 중심으로 조회 기간을 계산하고 차트 시계열 데이터를 가공하는 로직 구현
  - 이동평균 계산 로직을 공통 메서드로 구현하고, 기간(5일, 20일)을 파라미터로 받아 재사용할 수 있도록 구성
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
│   │               ├── aop/
│   │               │   └── TimeTraceAspect.java
│   │               ├── client/
│   │               │   ├── MarketIndexApiCacheService.java
│   │               │   └── MarketIndexApiClient.java
│   │               ├── config/
│   │               │   ├── MarketIndexApiConfig.java
│   │               │   ├── MarketIndexApiProperties.java
│   │               │   ├── OpenApiConfig.java
│   │               │   ├── QueryDSLConfig.java
│   │               │   └── SchedulingConfig.java
│   │               ├── controller/
│   │               │   ├── api/
│   │               │   │   ├── AutoSyncConfigApi.java
│   │               │   │   ├── DashboardApi.java
│   │               │   │   ├── IndexDataApi.java
│   │               │   │   ├── IndexInfoApi.java
│   │               │   │   └── IndexSyncApi.java
│   │               │   ├── AutoSyncConfigController.java
│   │               │   ├── DashboardController.java
│   │               │   ├── IndexDataController.java
│   │               │   ├── IndexInfoController.java
│   │               │   └── IndexSyncController.java
│   │               ├── dto/
│   │               │   ├── autosyncconfig/
│   │               │   │   ├── AutoSyncConfigDto.java
│   │               │   │   ├── AutoSyncConfigQueryCondition.java
│   │               │   │   └── AutoSyncConfigUpdateRequest.java
│   │               │   ├── dashboard/
│   │               │   │   ├── IndexChartDto.java
│   │               │   │   ├── IndexPerformanceDto.java
│   │               │   │   └── RankedIndexPerformanceDto.java
│   │               │   ├── indexdata/
│   │               │   │   ├── IndexDataCreateRequest.java
│   │               │   │   ├── IndexDataDto.java
│   │               │   │   ├── IndexDataExportRequest.java
│   │               │   │   ├── IndexDataQueryCondition.java
│   │               │   │   ├── IndexDataSortField.java
│   │               │   │   └── IndexDataUpdateRequest.java
│   │               │   ├── indexinfo/
│   │               │   │   ├── IndexInfoCreateRequest.java
│   │               │   │   ├── IndexInfoDto.java
│   │               │   │   ├── IndexInfoQueryCondition.java
│   │               │   │   ├── IndexInfoSummaryDto.java
│   │               │   │   └── IndexInfoUpdateRequest.java
│   │               │   ├── openapi/
│   │               │   │   ├── MarketIndexApiRequest.java
│   │               │   │   └── MarketIndexApiResponse.java
│   │               │   ├── response/
│   │               │   │   └── PageResponse.java
│   │               │   ├── sync/
│   │               │       ├── IndexDataSyncRequest.java
│   │               │       ├── IndexDataSyncSource.java
│   │               │       ├── IndexInfoLookup.java
│   │               │       ├── IndexInfoSyncSource.java
│   │               │       ├── SyncJobDto.java
│   │               │       └── SyncJobQueryCondition.java
│   │               ├── entity/
│   │               │   ├── base/
│   │               │   │   ├── BaseEntity.java
│   │               │   │   └── BaseUpdatableEntity.java
│   │               │   ├── AutoSyncConfig.java
│   │               │   ├── IndexData.java
│   │               │   ├── IndexInfo.java
│   │               │   └── IntegrationTask.java
│   │               ├── enums/
│   │               │   ├── AutoSyncConfigSortField.java
│   │               │   ├── ChartPeriodType.java
│   │               │   ├── IndexInfoSortField.java
│   │               │   ├── JobResult.java
│   │               │   ├── JobType.java
│   │               │   ├── PeriodType.java
│   │               │   ├── SourceType.java
│   │               │   └── SyncJobSortField.java
│   │               ├── exception/
│   │               │   ├── BusinessLogicException.java
│   │               │   ├── ErrorResponse.java
│   │               │   ├── ExceptionCode.java
│   │               │   └── GlobalExceptionHandler.java
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
│   │               │   │   │   ├── AutoSyncConfigCustomRepositoryImpl.java
│   │               │   │   │   ├── IndexDataCustomRepositoryImpl.java
│   │               │   │   │   ├── IndexInfoCustomRepositoryImpl.java
│   │               │   │   │   └── IntegrationTaskCustomRepositoryImpl.java
│   │               │   │   ├── AutoSyncConfigCustomRepository.java
│   │               │   │   ├── IndexDataCustomRepository.java
│   │               │   │   ├── IndexInfoCustomRepository.java
│   │               │   │   └── IntegrationTaskCustomRepository.java
│   │               │   ├── AutoSyncConfigRepository.java
│   │               │   ├── DashboardRepository.java
│   │               │   ├── IndexDataRepository.java
│   │               │   ├── IndexInfoRepository.java
│   │               │   └── IntegrationTaskRepository.java
│   │               ├── scheduler/
│   │               │   └── AutoSyncScheduler.java
│   │               ├── service/
│   │               │   ├── AutoSyncConfigService.java
│   │               │   ├── DashboardService.java
│   │               │   ├── IndexDataService.java
│   │               │   ├── IndexDataSyncFailureService.java
│   │               │   ├── IndexDataSyncService.java
│   │               │   ├── IndexInfoService.java
│   │               │   ├── IndexInfoSyncFailureService.java
│   │               │   ├── IndexInfoSyncService.java
│   │               │   ├── IndexSyncService.java
│   │               │   └── IntegrationTaskService.java
│   │               ├── util/
│   │               │   ├── ClientIpResolver.java
│   │               │   ├── IndexNameResolver.java
│   │               │   └── SortUtils.java
│   │               └── FindexApplication.java
│   ├── resources/
│       ├── static/
│       │   ├── assets/
│       │   │   ├── Pretendard-Black-B7X87vPW.woff2
│       │   │   ├── Pretendard-Black-CGKHU3YP.woff
│       │   │   ├── Pretendard-Bold-BYNivUXw.woff2
│       │   │   ├── Pretendard-Bold-DD7wHHNl.woff
│       │   │   ├── Pretendard-ExtraBold-C0vVUedy.woff2
│       │   │   ├── Pretendard-ExtraBold-DkRXFB8B.woff
│       │   │   ├── Pretendard-ExtraLight-Bi0YRlFr.woff2
│       │   │   ├── Pretendard-ExtraLight-CmnYHmfp.woff
│       │   │   ├── Pretendard-Light-BSr3DBFh.woff
│       │   │   ├── Pretendard-Light-knQmDAda.woff2
│       │   │   ├── Pretendard-Medium-Cs2k_Pp2.woff
│       │   │   ├── Pretendard-Medium-Dw2vNklR.woff2
│       │   │   ├── Pretendard-Regular-BhrLQoBv.woff2
│       │   │   ├── Pretendard-Regular-D5CgADJ9.woff
│       │   │   ├── Pretendard-SemiBold-ClEDdoZU.woff2
│       │   │   ├── Pretendard-SemiBold-SXfe8JY8.woff
│       │   │   ├── Pretendard-Thin-Cq3km6ap.woff
│       │   │   ├── Pretendard-Thin-DWJVAZ2K.woff2
│       │   │   ├── index-Bweg6EuF.css
│       │   │   └── index-D-ZKSpBz.js
│       │   ├── favico.ico
│       │   └── index.html
│       ├── application.yaml
│       ├── schema-h2.sql
│       └── schema-postgresql.sql
├── test/
    ├── java/
    │   ├── com/
    │       ├── sprint/
    │           ├── findex/
    │               ├── client/
    │               │   └── MarketIndexApiCacheServiceTest.java
    │               ├── dto/
    │               │   ├── autosyncconfig/
    │               │       └── AutoSyncConfigQueryConditionTest.java
    │               ├── scheduler/
    │               │   └── AutoSyncSchedulerTest.java
    │               ├── service/
    │               │   ├── AutoSyncConfigServiceTest.java
    │               │   ├── IndexInfoServiceTest.java
    │               │   └── IntegrationTaskServiceTest.java
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
- 변경 가능 
- [송시연]()
- [김민형]()
- [박성국]()
- [성주현]()
- [전창현]()
