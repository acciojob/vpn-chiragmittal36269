package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{

        String s = countryName.toUpperCase();
        boolean marker = true;
        if (s.equals("IND") || s.equals("USA") || s.equals("AUS") || s.equals("CHI") || s.equals("JPN")) {
            marker = true;
        } else {
            marker = false;
        }

        if (!marker) {
            throw new Exception("Country not found");
        }

        Country country = new Country();

//        switch (s) {
//            case "IND":
//                country.setCountryName(CountryName.IND);
//                country.setCode("001");
//                break;
//            case "USA":
//                country.setCountryName(CountryName.USA);
//                country.setCode("002");
//                break;
//            case "AUS":
//                country.setCountryName(CountryName.AUS);
//                country.setCode("003");
//                break;
//            case "CHI":
//                country.setCountryName(CountryName.CHI);
//                country.setCode("004");
//                break;
//            default:
//                country.setCountryName(CountryName.JPN);
//                country.setCode("005");
//                break;
//        }
        country.setCountryName(CountryName.valueOf(countryName));
        country.setCode(CountryName.valueOf(countryName).toCode());

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setOriginalIp(country.getCode()+"."+user.getId());
        user.setConnected(false);
        user.setMaskedIp(null);

        country.setUser(user);

        user.setCountry(country);

        return userRepository3.save(user);
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {

        ServiceProvider serviceProvider = serviceProviderRepository3.findById(serviceProviderId).get();
        User user = userRepository3.findById(userId).get();

        serviceProvider.getUsers().add(user);

        user.getServiceProviderList().add(serviceProvider);

        serviceProviderRepository3.save(serviceProvider);

        return user;
    }
}
