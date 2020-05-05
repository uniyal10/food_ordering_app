package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressDao addressDao;


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(AddressEntity addressEntity, CustomerAddressEntity customerAddressEntity) throws
            SaveAddressException {
        //validations

        if(valididatePincode(addressEntity.getPincode())==false) {
            throw new SaveAddressException("SAR-002", "Invalid pincode");
        }

        addressDao.saveAddress(addressEntity);
        addressDao.saveCustomerAddress(customerAddressEntity);
        return  addressEntity;
    }
    private boolean valididatePincode(String num){
        String pattern = "\\d{6}";
        Pattern pat = Pattern.compile(pattern);
        Matcher matcher = pat.matcher(num);
        if(!matcher.matches()) {
            return false;
        }

        return true;

    }

    @Override
    public AddressEntity getAddressByUUID(String addressId, CustomerEntity customerEntity) throws
            AuthorizationFailedException, AddressNotFoundException {
        //   if(addressDao.getAddressByUUID(addressId) == null) throw new AddressNotFoundException("ANF-003", "No address by this id");
        //   else if(addressDao.getCustomerByAddress(addressId).getCustomer() != customerEntity) throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");
        return addressDao.getAddressByUUID(addressId);
    }



    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAddressEntity saveCustomerAddress(CustomerAddressEntity customerAddressEntity) {
        return addressDao.saveCustomerAddress(customerAddressEntity);
    }


    @Override
    public List<AddressEntity> getAllAddress(CustomerEntity customer)  {
        return addressDao.getAllAddress(customer);
    }

    @Override
    public StateEntity getState(String stateUUID) throws AddressNotFoundException {
        StateEntity stateEntity = addressDao.getStateByUUID(stateUUID);
            if(stateEntity==null){
                throw new AddressNotFoundException("ANF-002","No state by this id");
            }
            return stateEntity;
    }


}