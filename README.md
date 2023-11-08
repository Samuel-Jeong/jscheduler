# jscheduler

~~~
* ScheduledThreadPoolExecutor 클래스와 비교
1. 작업 예약 주체와 작업 실행 주체가 독립적으로 실행되므로 동일한 Thread pool size 에서 ScheduledThreadPoolExecutor 보다 더 많은 작업 진행 가능
(작업 실행 주체의 개수를 개발자 마음대로 조절 가능 > 모든 작업에 대해 대응 가능)
2. 작업 정의를 Builder 패턴으로 구성하므로 더 개발자 친화적인 코딩 가능
3. ScheduledThreadPoolExecutor 는 작업을 한번 또는 무한하게 동작하도록 예약 가능하지만, JScheduler 는 작업을 한번이 아니라 일정 횟수만큼 돌고 종료하도록 예약할 수 있다.
4. 작업 큐는 우선순위 큐를 사용한다.
5. 작업 진행 중에 예외가 발생하거나 종료해야될 상황일 때 해당 작업을 종료시키고 싶으면, ScheduledThreadPoolExecutor 는 중간에 cancel 할 수 있지만 부 대기열에 배치되기 전에 다른 형식으로 변환된 작업이면 취소할 수 없어서 확실하게 종료하려면 자신에게 등록된 모든 작업을 중단(shutdown 함수 사용)해야 한다.
   하지만, JScheduler 다른 작업들과는 독립적으로 해당 작업만 종료시킬 수 있다. (어떤 상황이든지 특정 작업 중단 가능)
(It may fail to remove tasks that have been converted into other forms before being placed on the internal queue. For example, a task entered using submit might be converted into a form that maintains Future status.)
~~~

## STRUCTURE
  
![스크린샷 2021-11-24 오후 5 01 40](https://user-images.githubusercontent.com/37236920/143198145-cb7ee03c-fdf3-4184-a1f9-4a5623e8702f.png)
  
![스크린샷 2021-11-24 오후 5 02 01](https://user-images.githubusercontent.com/37236920/143198179-8b41328d-f520-4d4a-8cd3-8736688ec01f.png)
  
![스크린샷 2021-11-24 오후 5 02 19](https://user-images.githubusercontent.com/37236920/143198224-3b58ce7d-1f6a-4856-8d25-8b5abbf0cf83.png)
  
![스크린샷 2021-11-24 오후 5 02 36](https://user-images.githubusercontent.com/37236920/143198256-f87d559c-1899-49d4-a303-184c1a29a4ba.png)

  
