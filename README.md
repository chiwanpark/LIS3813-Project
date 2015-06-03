LIS3813-Project
===============

이 Git 저장소는 연세대학교 2015학년도 1학기 텍스트정보처리론 수업의 기말
프로젝트를 위한 저장소입니다. 조원은 김영효, 윤성근, 박치완입니다.

 

Prerequisites
-------------

-   Java 1.7 or Higher

-   Apache Maven 3

 

Project Architecture
--------------------

이 프로그램은 몇 개의 프로그램으로 구성되어 있습니다.

1.  Crawler - 멜론 홈페이지에서 노래 정보를 HTML 파일 형태로 다운받는 프로그램

2.  Extractor - 다운 받은 HTML 파일에서 노래 정보를 추출하는 프로그램

3.  Analysis - 가사 데이터를 바탕으로 Topic Modelling (based LDA)을 수행하는
    프로그램

4.  StatisticsByKey - Topic Modelling 결과를 바탕으로 연도별, 가수별, 작사가별
    선호하는 Topic 계산

5.  StatisticsByTopic - Topic Modelling 결과를 바탕으로 Topic별 가수, 연도,
    작사가가 발표한 곡의 수를 계산

 

How to Build and Run
--------------------

1.  프로젝트 최상위 디렉토리에서 아래 명령을 입력하면, 프로그램을 빌드 할 수
    있습니다.  
    `mvn clean package -DskipTests`

2.  빌드를 완료하면, `target` 디렉토리 아래에 2개의 jar 파일이 생성됩니다.
    `LIS3813-Project-<VERSION>.jar` 파일은 외부 라이브러리가 포함되지 않은
    파일이고, `LIS3813-Project-<VERSION>-jar-with-dependencies.jar` 파일은
    프로젝트에서 사용한 모든 라이브러리가 함께 들어있는 파일입니다.

3.  각각의 프로그램마다 실행하는 방법이 다릅니다.

    1.  Cralwer  
        `java -jar LIS3813-Project-<VERSION>.jar crawler <시작 노래 번호> <끝
        노래 번호> <결과 저장 경로>`

    2.  Extractor  
        `java -jar LIS3813-Project-<VERSION>.jar extractor <Crawler 저장 경로>
        <결과 저장 경로>`

    3.  Analysis  
        `java -jar LIS3813-Project-<VERSION>.jar topicModeling <Extractor 저장
        경로> <결과 저장 경로> <Topic 갯수> <Top Words 갯수> <반복 횟수> <Thread
        갯수> <분석을 하려는 특정 연도 (전체는 -1)>`

    4.  StatisticsByKey  
        `java -jar LIS3813-Project-<VERSION>.jar statisticsByKey <Topic Modeling
        결과 경로> <Extractor 결과 경로> <통계를 내고자 하는 Key (artists,
        lyricists, date)> <결과 저장 경로>`

    5.  StatisticsByTopic  
        `java -jar LIS3813-Project-<VERSION>.jar statisticsByTopic <Topic
        Modeling 결과 경로> <Extractor 결과 경로> <결과 저장 경로>`

 
