package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.*;

import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CustomerServiceImpl implements CustomerService {


    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(CustomerEntity customerEntity) throws SignUpRestrictedException {
        if( valididateEmail(customerEntity.getEmailAddress())==false){
            throw new SignUpRestrictedException("SGR-002","Invalid email-id format!");
        }


        if(valididateNumber(customerEntity.getContactNumber())==false){

            throw new SignUpRestrictedException("SGR-003","Invalid contact number!");
        }

        if(valididatePassword(customerEntity.getPassword())==false){
            throw  new SignUpRestrictedException("SGR-004","Weak password!");
        }

        CustomerEntity customerEntity1 =  customerDao.getCustomerByContactNumber(customerEntity.getContactNumber());

        if(customerEntity1 != null) {
                 throw new SignUpRestrictedException("SGR-001","This contact number is already registered! Try other contact number.");
        }


        String[] encryptPassoword = passwordCryptographyProvider.encrypt(customerEntity.getPassword());

        customerEntity.setSalt(encryptPassoword[0]);
        customerEntity.setPassword(encryptPassoword[1]);

        return customerDao.saveCustomer(customerEntity);

    }


    private boolean valididateEmail(String email) {

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        Matcher matcher = pat.matcher(email);
        if(!matcher.matches()) {
            return false;
        }

        return true;
    }

    private boolean valididateNumber(String num){
        String pattern = "\\d{6,10}";
        Pattern pat = Pattern.compile(pattern);
        Matcher matcher = pat.matcher(num);
        if(!matcher.matches()) {
            return false;
        }

        return true;

    }
    public boolean valididatePassword(String pwd){
        String pattern="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        Pattern pat = Pattern.compile(pattern);
        Matcher matcher = pat.matcher(pwd);
        if(!matcher.matches()) {
            return false;
        }

        return true;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity login(String contactNumber, String password) throws AuthenticationFailedException {

        if(valididateNumber(contactNumber)==false){
            throw new AuthenticationFailedException("ATH-003","Incorrect format of decoded customer name and password");
        }

        CustomerEntity customerEntity = null;
        customerEntity = customerDao.getCustomerByContactNumber(contactNumber);

        if (customerEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }

        final String encryptedPassword = PasswordCryptographyProvider.encrypt(password, customerEntity.getSalt());


        if (encryptedPassword.equals(customerEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
            customerAuthEntity.setUuid(UUID.randomUUID().toString());
            customerAuthEntity.setCustomer(customerEntity);
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime expiresAt = now.plusHours(8);

            customerAuthEntity.setLoginAt(ZonedDateTime.now());
            customerAuthEntity.setExpiresAt(expiresAt);
            customerAuthEntity.setAccessToken(jwtTokenProvider.generateToken(customerEntity.getUuid(), now, expiresAt));
            return customerDao.saveCustomerAuth(customerAuthEntity);
        } else {
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }
    }

    public CustomerAuthEntity authorization(String access_token) throws AuthorizationFailedException {

        CustomerAuthEntity customerAuthEntity = customerDao.getCustomerAuthByAccesstoken(access_token);

        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        if (customerAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        if (ZonedDateTime.now().isAfter(customerAuthEntity.getExpiresAt())) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }
        return customerAuthEntity;
    }

    @Override
    public CustomerEntity getCustomer(String access_token) throws AuthorizationFailedException {

        authorization(access_token);
        CustomerAuthEntity customerAuthEntity = customerDao.getCustomerAuthByAccesstoken(access_token);
        return customerAuthEntity.getCustomer();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void logout(String accessToken) throws AuthorizationFailedException {


        CustomerAuthEntity customerAuthEntity = authorization(accessToken);
        customerAuthEntity.setLogoutAt(ZonedDateTime.now());

         customerDao.updateCustomerAuthEntity(customerAuthEntity);

    }
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity password(String accessToken,String oldpwd,String newPwd) throws AuthorizationFailedException, UpdateCustomerException {
       CustomerAuthEntity customerAuthEntity = authorization(accessToken);
       if(valididatePassword(newPwd)==false){
           throw new  UpdateCustomerException("UCR-001","Weak password!");
       }
       CustomerEntity customerEntity = customerAuthEntity.getCustomer();
       final String encryptedPassword = PasswordCryptographyProvider.encrypt(oldpwd, customerEntity.getSalt());
       if(!encryptedPassword.equals(customerEntity.getPassword())){
           throw  new UpdateCustomerException("UCR-004","Incorrect old password!)");
       }
        String[] encryptPassoword = passwordCryptographyProvider.encrypt(newPwd);

        customerEntity.setSalt(encryptPassoword[0]);
        customerEntity.setPassword(encryptPassoword[1]);
       return customerDao.saveCustomer(customerEntity);
    }
}