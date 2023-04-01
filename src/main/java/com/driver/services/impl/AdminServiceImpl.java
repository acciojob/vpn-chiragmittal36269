package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);

        Admin updatedAdmin = adminRepository1.save(admin);

        return updatedAdmin;
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {

        Admin admin = adminRepository1.findById(adminId).get();

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setName(providerName);

        admin.getServiceProviders().add(serviceProvider);

        serviceProvider.setAdmin(admin);

        return adminRepository1.save(admin);
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception {
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

        country.setUser(null);

        ServiceProvider serviceProvider = serviceProviderRepository1.findById(serviceProviderId).get();
        serviceProvider.getCountryList().add(country);

        country.setServiceProvider(serviceProvider);

        return serviceProviderRepository1.save(serviceProvider);
    }
}
