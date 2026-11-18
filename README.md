# ERP 백엔드

## 최초 시스템 관리자 생성

SQLite 백엔드를 처음 실행하기 전에 아래 환경 변수를 설정하세요. 로컬에서 IntelliJ를 사용한다면 **Run → Edit Configurations → ERP 백엔드 실행 항목 → Environment variables**에서 설정할 수 있습니다.

| 환경 변수 | 입력값 |
| --- | --- |
| `ERP_ADMIN_EMAIL` | 로그인에 사용할 새 이메일 |
| `ERP_ADMIN_PASSWORD` | 로그인에 사용할 비밀번호 |

실행 시 DB에 `SYSTEM_ADMIN` 계정이 없으면 입력한 값으로 계정을 생성합니다. 생성 후에는 같은 이메일과 비밀번호로 ERP에 로그인할 수 있습니다.

이미 시스템 관리자가 있으면 초기 생성 절차를 건너뜁니다. 환경 변수 두 개에 모두 값이 없으면 서버는 평소대로 시작하고 계정 설정 방법을 로그에 남깁니다.

Render에서는 ERP 백엔드 서비스의 **Environment → Add Environment Variable**에서 같은 이름으로 추가합니다. 최초 관리자 생성 코드가 포함된 버전을 배포한 뒤 로그에서 `최초 시스템 관리자 계정이 생성되었습니다.` 메시지를 확인할 수 있습니다.

SQLite 파일이 재배포 후에도 유지되는 환경에서는 계정을 생성한 뒤 초기 생성용 변수를 제거해도 됩니다. Render에서 SQLite 파일이 영구 디스크에 저장되지 않으면 재시작이나 재배포 때 계정과 다른 DB 변경 사항이 초기화될 수 있습니다.

## 비밀번호 찾기 메일

ERP 백엔드는 가입한 이메일 주소로 임시 비밀번호를 Gmail API를 통해 보냅니다. 발송 계정으로 `https://www.googleapis.com/auth/gmail.send` 권한을 승인한 뒤 다음 값을 Render의 **Environment**나 로컬 백엔드 실행 환경에 설정하세요.

| 환경 변수 | 입력값 |
| --- | --- |
| `ERP_GMAIL_CLIENT_ID` | 메일 발송용 Google OAuth 클라이언트 ID |
| `ERP_GMAIL_CLIENT_SECRET` | 메일 발송용 OAuth 클라이언트 보안 비밀번호 |
| `ERP_GMAIL_REFRESH_TOKEN` | 발송 계정으로 발급받은 갱신 토큰 |
| `ERP_GMAIL_SENDER` | 발송 권한을 승인한 Gmail 주소 |

SQLite 백엔드를 로컬에서 실행한다면 Git이 무시하는 루트의 `.env.oauth.properties`에 같은 이름으로 설정해도 됩니다. 발송 계정의 OAuth 프로젝트가 외부 사용자 대상의 테스트 상태라면 갱신 토큰은 7일 뒤 만료됩니다.
