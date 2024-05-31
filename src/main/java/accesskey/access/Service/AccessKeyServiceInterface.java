package accesskey.access.Service;

import accesskey.access.Entity.AccessKey;
import accesskey.access.Entity.KeyDetails;

import java.util.List;

public interface AccessKeyServiceInterface {

     List<KeyDetails> getAllAccessKeys();

     List<KeyDetails> getAllAccessKeysByEmail(String email);

     KeyDetails getActiveAccessKeyByEmail(String email);
}
