@Slf4j
@Component
@RequiredArgsConstructor
public class AutoSyncScheduler {
    @Scheduled(fixedDelayString = "${app.scheduler.sync-delay-ms}")
    public void syncIndexData() {
        log.info("자동 연동 스케줄러 실행");
    }
}
