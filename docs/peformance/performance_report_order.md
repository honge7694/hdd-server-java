# 재고 감소 성능 보고서

## 테스트 요약
- 총 요청 : 100 * 2회, 200 * 1회(비관적 락), 100 * 2회, 200 * 1회(낙관적 락)
- 동시 요청 스레드 : 50, 100회

## 결과 요약
| 락종류              | 총 요청 수 | 성공  | 실패 | 소요 시간(ms) |
|:-----------------|:-------|:----|:---|:----------|
| 비관적락(Thread 100) | 100    | 98  | 2  | 13,481    |
| 비관적락(Thread 100) | 100    | 98  | 2  | 14,631    |
| 비관적락(Thread 200) | 100    | 100 | 0  | 6541      |
| 낙관적락(Thread 100) | 100    | 13  | 87 | 22,114    |
| 낙관적락(Thread 100) | 100    | 14  | 86 | 22,954    |
| 낙관적락(Thread 200) | 100    | 14  | 86 | 16,597    |


## 비관적락 과정
1. `@Lock(LockModeType.PESSIMISTIC_WRITE)`만 사용하였을 시 `timed out` 발생하여, `appication.yml`파일 `maximum-pool-size: 100`으로 수정
2. `ConflictException`이 발생하며, 요청에 대한 성공 수 1을 반환, 이를 해결하기 위해 주문 중복 멱등성 확인 분리
3. `HangHaePlusDataSource - Connection is not available...` 에러를 해결하기 위해 `maximum-pool-size: 200`으로 수정

## 낙관적락 과정
1. `@Version`을 추가하여 낙관적락 시도하였지만 동시에 요청이 들어와 대부분의 요청 실패
2. 재시도 로직을 추가
3. `ObjectOptimisticLockingFailureException` 발생하여 Thread를 늘려보았지만 실패.
4. `Thread.sleep() 100 -> 300`으로 진행하였지만, 효과 미미함.


## 결론
- 비관적 락은 트랜잭션 간 충돌을 DB에서 제어하기 때문에 성공률이 높았지만, 커넥션 풀 고갈 문제가 발생하여 `maximum-pool-size` 조정이 필요했습니다.
- `ObjectOptimisticLockingFailureException`이슈를 재시도 로직으로도 해결할 수 없었고, 대부분 실패하거나 성능이 저하되었습니다.
