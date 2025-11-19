package org.labs.lab6_auth.service;

import org.labs.lab6_auth.entity.User;
import org.springframework.stereotype.Service;
import org.labs.lab6_auth.repository.UserRepository;
import org.labs.lab6_auth.config.TotpUtil;

@Service
public class TOTPService {
    private final UserRepository userRepository;

    public TOTPService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void prepareSecretForUser(User user) {
        String secret = TotpUtil.generateSecret();
        user.setTwoFactorSecret(secret);
        userRepository.save(user);
    }

    public boolean verifyCode(User user, String codeStr) {
        if(user.getTwoFactorSecret() == null) return false;
        int code;
        try{
            code = Integer.parseInt(codeStr);
        }catch(NumberFormatException e){
            return false;
        }
        return TotpUtil.verifyCode(user.getTwoFactorSecret(), code, 1);
    }

    public void enableTwoFactor(User user) {
        user.setTwoFactorEnabled(true);
        userRepository.save(user);
    }

    public void disableTwoFactor(User user) {
        user.setTwoFactorEnabled(false);
        user.setTwoFactorSecret(null);
        userRepository.save(user);
    }


}
