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
|<img src="https://user-images.githubusercontent.com/78714391/125157744-96292900-e1a7-11eb-8d27-1af22ab6721c.jpg" width="60%" height="60%"></img><br/>|일정의 상세 정보를 볼 수 있습니다.<br>일정 메모를 볼 수 있으며 상태 `None` `InProgress` `Completed` `NotCompleted` 설정을 할 수 있습니다.<br>우측 상단의 수정 버튼을 눌러 `ScheduleAddActivity` 수정이 가능합니다.|

## ScheduleAddActivity
#### 기능 설명
|화면|설명|
|:---:|---|
|<img src="https://user-images.githubusercontent.com/78714391/125158497-95df5c80-e1ac-11eb-8d48-938bb4386ed1.png" width="52%" height="52%"></img><br/>|일정을 추가하거나 수정합니다.<br>날짜와 일정 이름, 메모, 색을 선택하고 저장 버튼을 누르면 일정이 저장됩니다.<br>일정 수정 시에는 우측 상단의 삭제 버튼을 눌러 삭제할 수 있습니다.|

## StudyPopupActivity
#### 기능 설명
|화면|설명|
|:---:|---|
|<img src="https://user-images.githubusercontent.com/78714391/125158517-b0b1d100-e1ac-11eb-9d17-53d08c6aef63.png" width="52%" height="52%"></img><br/>|과목의 공부량을 일별로 확인할 수 있습니다. 또한 목표량까지 남은 시간은 확인할 수도 있습니다.<br><br>하단 학습 시작 버튼을 눌러 `StudyActivity` 기록을 시작할 수 있습니다.<br>우측 상단 수정 버튼을 눌러 `StudyAddActivity` 과목 정보를 수정할 수 있습니다.|

## StudyAddActivity
#### 기능 설명
|화면|설명|
|:---:|---|
|<img src="https://user-images.githubusercontent.com/78714391/125158848-98db4c80-e1ae-11eb-87d1-39e7c391de9a.jpg" width="60%" height="60%"></img><br/>|과목을 추가하거나 수정할 수 있습니다.<br>수정의 경우는 우측 상단의 삭제 버튼을 눌러 삭제할 수 있습니다.|
|<img src="https://user-images.githubusercontent.com/78714391/125158895-e2c43280-e1ae-11eb-8e4b-56e4d9f10e42.png" width="60%" height="60%"></img><br/>|아이콘 수정 버튼을 누르면 아이콘과 색을 선택할 수 있는 하단 다이얼로그가 나타납니다.|

## StudyActivity
#### 기능 설명
|화면|설명|
|:---:|---|
|<img src="https://user-images.githubusercontent.com/78714391/125159016-b3fa8c00-e1af-11eb-8973-3327db77bd11.png" width="60%" height="60%"></img><br/>|공부 모드 화면입니다. 공부를 시작한지 몇 시간이 되었는지 알 수 있습니다.<br>하단 완료 버튼으로 시간 저장을, 그 아래 취소 버튼으로 저장하지 않고 취소를 할 수 있습니다.<br><br>공부 모드에서는 이 화면만 보여집니다.|

## AccountActivity
#### 기능 설명
|화면|설명|
|:---:|---|
|<img src="https://user-images.githubusercontent.com/78714391/125159127-69c5da80-e1b0-11eb-8e15-ad706b6912e5.png" width="70%" height="70%"></img><br/>|계정 정보와 공부 시간을 모두 모아서 볼 수 있는 화면입니다. 익명 로그인 상태에서는 소셜 로그인 버튼이 중앙에, 소셜 로그인 상테에서는 로그아웃 버튼이 우측 상단에 표시됩니다.|

