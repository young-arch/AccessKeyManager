package accesskey.access.Schedule;

import accesskey.access.Entity.AccessKey;
import accesskey.access.Repository.AccessKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

//Automatically handles updating expired keys
@Component
public class AccessKeyScheduler {

    @Autowired
    private AccessKeyRepository accessKeyRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void updateExpiredKeys(){
        LocalDateTime now = LocalDateTime.now();
        List<AccessKey> expiredKeys =  accessKeyRepository.findAllByExpiryDateBefore(now);

        if (!expiredKeys.isEmpty()){
            List<Integer> expiredKeysIds = expiredKeys.stream()
                    .map(AccessKey :: getId)
                    .collect(Collectors.toList());
                    accessKeyRepository.updateStatusByIds(expiredKeysIds,AccessKey.AccessKeyStatus.EXPIRED);
        }
    }


}
