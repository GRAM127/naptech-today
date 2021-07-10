# Today
> 네, 저도 알고 있습니다. 코드가 많이 난장판입니다...<br>
> 교내 대회 출품작입니다.

일정과 공부를 같이 볼 수 있도록 만든 앱, **Today** 입니다.<br>
최대한 간단하게 일정을 관리할 수 있도록 만드는 것을 목표로 하였습니다.<br>
<br>
[Firebase](firebase.google.com/)를 이용한 클라우드를 이용해 동일 계정간 강력한 연동성을 지원합니다.
- 한 기기에서 데이터 변경 시 타 기기에 즉시 반영됩니다
- 데이터별 데이터 접근 권한을 다르게 함으로서 개인 데이터를 안전하게 저장합니다.

#### Code 설명
###### 주요 클래스
```kotlin
TODO("")
val firebaseLogin:  FirebaseLogin // Firebase 로그인 관리를 위한 객체를 저장
```

## MainActivity
#### 기능 설명
|화면|설명|
|:---:|---|
|<img src="https://user-images.githubusercontent.com/78714391/125157666-52362400-e1a7-11eb-88d8-5c009c3d5e93.jpg" width="70%" height="70%"></img><br/>`ScheduleFragment`|사용자가 추가해놓은 일정과 진행 상태 `None` `InProgress` `Completed` `NotCompleted` 확인할 수 있습니다.<br>일정이 많이 있을 때 위의 날짜를 클릭하면 그 날짜의 일정 위치로 바로 이동합니다.<br><br>일정을 클릭하면 `SchedulePopupActivity` 일정 정보를 볼 수 있습니다<br>진행 상태를 클릭해 진행 상태를 바로 변경할 수 있습니다.|
|<img src="https://user-images.githubusercontent.com/78714391/125154701-3d9d6000-e196-11eb-9834-322abbd01356.png" width="70%" height="70%"></img><br/>`StudyFragment`|사용자가 추가해놓은 과목과 일별 공부 진행도를 확인할 수 있습니다.<br>과목을 눌러 `StudyPopupActivity` 자세한 과목 정보를 볼 수 있습니다.|
|<img src="https://user-images.githubusercontent.com/78714391/125156483-9bcf4080-e1a0-11eb-8fb7-421201bdcb53.jpg" width="70%" height="70%"></img><br/>|앱을 처음 이용할 때 자동으로 익명 계정을 부여합니다. 이를 소셜 계정 `Google` 으로 업데이트하도록 하단 다이얼로그로 유도합니다.<br><br>우측 상단의 계정 버튼을 눌러 `AccountActivity` 소셜 계정으로 업데이트 할 수도 있습니다.|

## SchedulePopupActivity
#### 기능 설명
|화면|설명|
|:---:|---|
|<img src="https://user-images.githubusercontent.com/78714391/125157744-96292900-e1a7-11eb-8d27-1af22ab6721c.jpg" width="70%" height="70%"></img><br/>|일정의 상세 정보를 볼 수 있습니다.<br>일정 메모를 볼 수 있으며 상태 `None` `InProgress` `Completed` `NotCompleted` 설정을 할 수 있습니다.<br>우측 상단의 수정 버튼을 눌러 `ScheduleAddActivity` 수정이 가능합니다.|

## ScheduleAddActivity
#### 기능 설명
|화면|설명|
|:---:|---|
|<img src="https://user-images.githubusercontent.com/78714391/125158118-d38eb600-e1a9-11eb-8552-8230493c8f87.jpg" width="70%" height="70%"></img><br/>|일정을 추가하거나 수정합니다.<br>날짜와 일정 이름, 메모, 색을 선택하고 저장 버튼을 누르면 일정이 저장됩니다.<br>일정 수정 시에는 우측 상단의 삭제 버튼을 눌러 삭제할 수 있습니다.|

