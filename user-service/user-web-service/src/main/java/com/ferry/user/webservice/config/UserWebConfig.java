package com.ferry.user.webservice.config;

import com.ferry.user.core.customer.delete.CustomerDeleteGateway;
import com.ferry.user.core.customer.delete.CustomerDeleteUseCase;
import com.ferry.user.core.customer.delete.DefaultCustomerDeleteUseCase;
import com.ferry.user.core.customer.detail.CustomerDetailGateway;
import com.ferry.user.core.customer.detail.CustomerDetailUseCase;
import com.ferry.user.core.customer.detail.DefaultCustomerDetailUseCase;
import com.ferry.user.core.customer.verification.CustomerVerificationGateway;
import com.ferry.user.core.customer.verification.CustomerVerificationUseCase;
import com.ferry.user.core.customer.verification.DefaultCustomerVerificationUseCase;
import com.ferry.user.core.customer.list.CustomerListGateway;
import com.ferry.user.core.customer.list.CustomerListUseCase;
import com.ferry.user.core.customer.list.DefaultCustomerListUseCase;
import com.ferry.user.core.customer.registration.CustomerRegistrationGateway;
import com.ferry.user.core.customer.registration.CustomerRegistrationUseCase;
import com.ferry.user.core.customer.registration.DefaultCustomerRegistrationUseCase;
import com.ferry.user.core.customer.update.CustomerUpdateGateway;
import com.ferry.user.core.customer.update.CustomerUpdateUseCase;
import com.ferry.user.core.customer.update.DefaultCustomerUpdateUseCase;
import com.ferry.user.core.staff.delete.DefaultStaffDeleteUseCase;
import com.ferry.user.core.staff.delete.StaffDeleteGateway;
import com.ferry.user.core.staff.delete.StaffDeleteUseCase;
import com.ferry.user.core.staff.detail.DefaultStaffDetailUseCase;
import com.ferry.user.core.staff.detail.StaffDetailGateway;
import com.ferry.user.core.staff.detail.StaffDetailUseCase;
import com.ferry.user.core.staff.forgotpassword.DefaultStaffForgottenPasswordUseCase;
import com.ferry.user.core.staff.forgotpassword.StaffForgottenPasswordGateway;
import com.ferry.user.core.staff.forgotpassword.StaffForgottenPasswordUseCase;
import com.ferry.user.core.staff.list.DefaultStaffListUseCase;
import com.ferry.user.core.staff.list.StaffListGateway;
import com.ferry.user.core.staff.list.StaffListUseCase;
import com.ferry.user.core.staff.login.DefaultStaffLoginUseCase;
import com.ferry.user.core.staff.login.StaffLoginGateway;
import com.ferry.user.core.staff.login.StaffLoginUseCase;
import com.ferry.user.core.staff.logout.DefaultStaffLogoutUseCase;
import com.ferry.user.core.staff.logout.StaffLogoutGateway;
import com.ferry.user.core.staff.logout.StaffLogoutUseCase;
import com.ferry.user.core.staff.refreshtoken.DefaultStaffRefreshTokenUseCase;
import com.ferry.user.core.staff.refreshtoken.StaffRefreshTokenGateway;
import com.ferry.user.core.staff.refreshtoken.StaffRefreshTokenUseCase;
import com.ferry.user.core.staff.registration.DefaultStaffRegistrationUseCase;
import com.ferry.user.core.staff.registration.StaffRegistrationGateway;
import com.ferry.user.core.staff.registration.StaffRegistrationUseCase;
import com.ferry.user.core.staff.resetpassword.DefaultStaffResetPasswordUseCase;
import com.ferry.user.core.staff.resetpassword.StaffResetPasswordGateway;
import com.ferry.user.core.staff.resetpassword.StaffResetPasswordUseCase;
import com.ferry.user.core.staff.submitotp.DefaultStaffSubmitOtpUseCase;
import com.ferry.user.core.staff.submitotp.StaffSubmitOtpUseCase;
import com.ferry.user.core.staff.update.DefaultStaffUpdateUseCase;
import com.ferry.user.core.staff.update.StaffUpdateGateway;
import com.ferry.user.core.staff.update.StaffUpdateUseCase;
import com.ferry.user.core.tenant.confirmregistration.DefaultTenantConfirmRegistrationUseCase;
import com.ferry.user.core.tenant.confirmregistration.TenantConfirmRegistrationGateway;
import com.ferry.user.core.tenant.confirmregistration.TenantConfirmRegistrationUseCase;
import com.ferry.user.core.tenant.expiration.DefaultTenantExpirationUseCase;
import com.ferry.user.core.tenant.expiration.TenantExpirationGateway;
import com.ferry.user.core.tenant.expiration.TenantExpirationUseCase;
import com.ferry.user.core.tenant.resendconfirmation.DefaultTenantResendConfirmationUseCase;
import com.ferry.user.core.tenant.resendconfirmation.TenantResendConfirmationGateway;
import com.ferry.user.core.tenant.resendconfirmation.TenantResendConfirmationUseCase;
import com.ferry.user.core.tenant.registration.DefaultTenantRegistrationUseCase;
import com.ferry.user.core.tenant.registration.UserEmailPublisher;
import com.ferry.user.core.tenant.registration.TenantRegistrationGateway;
import com.ferry.user.core.tenant.registration.TenantRegistrationUseCase;
import com.ferry.user.core.tenant.registration.TurnstileVerificationGateway;
import com.ferry.user.core.tools.CryptoConstant;
import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.core.tools.TokenProcessor;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.gateway.customer.CustomerDeleteJpaGateway;
import com.ferry.user.gateway.customer.CustomerDetailJpaGateway;
import com.ferry.user.gateway.customer.CustomerVerificationJpaGateway;
import com.ferry.user.gateway.customer.CustomerListJpaGateway;
import com.ferry.user.gateway.customer.CustomerRegistrationJpaGateway;
import com.ferry.user.gateway.customer.CustomerUpdateJpaGateway;
import com.ferry.user.gateway.customer.repository.CustomerAddressJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerEmailJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerPhoneJpaRepository;
import com.ferry.user.gateway.notification.UserEmailRedisPublisher;
import com.ferry.user.gateway.notification.repository.EmailTriggerJpaRepository;
import com.ferry.user.gateway.notification.repository.EmailTriggerStatusJpaRepository;
import com.ferry.user.gateway.notification.repository.EmailTriggerTypeJpaRepository;
import com.ferry.user.gateway.session.repository.UserSessionJpaRepository;
import com.ferry.user.gateway.session.repository.UserSessionTypeJpaRepository;
import com.ferry.user.gateway.staff.*;
import com.ferry.user.gateway.staff.repository.*;
import com.ferry.user.gateway.tenant.CloudflareTurnstileGateway;
import com.ferry.user.gateway.tenant.TenantConfirmRegistrationJpaGateway;
import com.ferry.user.gateway.tenant.TenantExpirationJpaGateway;
import com.ferry.user.gateway.tenant.TenantRegistrationJpaGateway;
import com.ferry.user.gateway.tenant.TenantResendConfirmationJpaGateway;
import com.ferry.user.gateway.tenant.repository.TenantJpaRepository;
import com.ferry.user.gateway.tenant.repository.TenantStatusJpaRepository;
import com.ferry.user.webservice.tenant.expiration.TenantExpirationScheduler;
import com.ferry.user.webservice.tools.Argon2PasswordTool;
import com.ferry.user.webservice.tools.DefaultUserCacheManager;
import com.ferry.user.gateway.notification.entity.EmailTriggerJpaEntity;
import com.ferry.user.gateway.staff.entity.StaffAddressJpaEntity;
import com.ferry.user.gateway.staff.entity.StaffEmailJpaEntity;
import com.ferry.user.gateway.staff.entity.StaffPhoneJpaEntity;
import com.ferry.utils.cache.CacheHandler;
import com.ferry.utils.cache.DefaultCacheHandler;
import com.ferry.utils.crypto.AesGcmCryptoTool;
import com.ferry.utils.crypto.CryptoKeyConfig;
import com.ferry.utils.crypto.CryptoTool;
import com.ferry.utils.generator.IdGenerator;
import com.ferry.utils.generator.UlidGenerator;
import com.ferry.utils.json.DefaultJsonManager;
import com.ferry.utils.json.JsonManager;
import com.password4j.Argon2Function;
import com.password4j.types.Argon2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password4j.Argon2Password4jPasswordEncoder;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Slf4j
@Configuration
@Lazy
@EnableConfigurationProperties({CryptoKeysProperties.class, InternalKeysProperties.class})
public class UserWebConfig{

	@Bean
	CryptoTool cryptoTool(CryptoKeysProperties cryptoKeysProperties, CacheHandler cacheHandler){
		CryptoKeyConfig keyConfig = CryptoKeyConfig.of(cryptoKeysProperties.activeKeyId(),
				cryptoKeysProperties.keys(), cryptoKeysProperties.blindIndexKey(),
				cryptoKeysProperties.allowPlaintextRead());
		return new AesGcmCryptoTool(keyConfig, () -> {
			try{
				return cacheHandler.get(CryptoConstant.ACTIVE_KEY_ID_KEY).orElse(null);
			}catch(RuntimeException e){
				log.warn("Failed to read {} from cache, falling back to the configured active key id",
						CryptoConstant.ACTIVE_KEY_ID_KEY, e);
				return null;
			}
		});
	}

//	@Bean
	ApplicationRunner cryptoBackfillRunner(StaffEmailJpaRepository staffEmailJpaRepository,
	                                       StaffPhoneJpaRepository staffPhoneJpaRepository,
	                                       StaffAddressJpaRepository staffAddressJpaRepository,
	                                       EmailTriggerJpaRepository emailTriggerJpaRepository,
	                                       CryptoTool cryptoTool){
		return _ -> {
			List<StaffEmailJpaEntity> emails = staffEmailJpaRepository.findAll();
			emails.forEach(entity -> entity.backfill(cryptoTool));
			staffEmailJpaRepository.saveAll(emails);
			List<StaffPhoneJpaEntity> phones = staffPhoneJpaRepository.findAll();
			phones.forEach(entity -> entity.backfill(cryptoTool));
			staffPhoneJpaRepository.saveAll(phones);
			List<StaffAddressJpaEntity> addresses = staffAddressJpaRepository.findAll();
			addresses.forEach(entity -> entity.backfill(cryptoTool));
			staffAddressJpaRepository.saveAll(addresses);
			List<EmailTriggerJpaEntity> triggers = emailTriggerJpaRepository.findAll();
			triggers.forEach(entity -> entity.backfill(cryptoTool));
			emailTriggerJpaRepository.saveAll(triggers);
			log.info("Crypto backfill done: {} staff emails, {} staff phones, {} staff addresses, {} email triggers",
					emails.size(), phones.size(), addresses.size(), triggers.size());
		};
	}

	@Bean
	CustomerRegistrationGateway customerRegistrationGateway(CustomerJpaRepository customerJpaRepository,
	                                                        CustomerEmailJpaRepository customerEmailJpaRepository,
	                                                        CustomerPhoneJpaRepository customerPhoneJpaRepository,
	                                                        CustomerAddressJpaRepository customerAddressJpaRepository,
	                                                        TenantJpaRepository tenantJpaRepository,
	                                                        IdGenerator idGenerator,
	                                                        CryptoTool cryptoTool){
		return new CustomerRegistrationJpaGateway(customerJpaRepository, customerEmailJpaRepository,
				customerPhoneJpaRepository, customerAddressJpaRepository, tenantJpaRepository, idGenerator,
				cryptoTool);
	}

	@Bean
	CustomerRegistrationUseCase customerRegistrationUseCase(CustomerRegistrationGateway customerRegistrationGateway){
		return new DefaultCustomerRegistrationUseCase(customerRegistrationGateway);
	}

	@Bean
	CustomerListGateway customerListGateway(CustomerJpaRepository customerJpaRepository,
	                                        CustomerEmailJpaRepository customerEmailJpaRepository,
	                                        CustomerPhoneJpaRepository customerPhoneJpaRepository,
	                                        CustomerAddressJpaRepository customerAddressJpaRepository,
	                                        CryptoTool cryptoTool){
		return new CustomerListJpaGateway(customerJpaRepository, customerEmailJpaRepository,
				customerPhoneJpaRepository, customerAddressJpaRepository, cryptoTool);
	}

	@Bean
	CustomerListUseCase customerListUseCase(CustomerListGateway customerListGateway){
		return new DefaultCustomerListUseCase(customerListGateway);
	}

	@Bean
	CustomerDetailGateway customerDetailGateway(CustomerJpaRepository customerJpaRepository,
	                                            CustomerEmailJpaRepository customerEmailJpaRepository,
	                                            CustomerPhoneJpaRepository customerPhoneJpaRepository,
	                                            CustomerAddressJpaRepository customerAddressJpaRepository,
	                                            CryptoTool cryptoTool){
		return new CustomerDetailJpaGateway(customerJpaRepository, customerEmailJpaRepository,
				customerPhoneJpaRepository, customerAddressJpaRepository, cryptoTool);
	}

	@Bean
	CustomerDetailUseCase customerDetailUseCase(CustomerDetailGateway customerDetailGateway){
		return new DefaultCustomerDetailUseCase(customerDetailGateway);
	}

	@Bean
	CustomerUpdateGateway customerUpdateGateway(CustomerJpaRepository customerJpaRepository,
	                                            CustomerEmailJpaRepository customerEmailJpaRepository,
	                                            CustomerPhoneJpaRepository customerPhoneJpaRepository,
	                                            CustomerAddressJpaRepository customerAddressJpaRepository,
	                                            TenantJpaRepository tenantJpaRepository,
	                                            IdGenerator idGenerator,
	                                            CryptoTool cryptoTool){
		return new CustomerUpdateJpaGateway(customerJpaRepository, customerEmailJpaRepository,
				customerPhoneJpaRepository, customerAddressJpaRepository, tenantJpaRepository, idGenerator,
				cryptoTool);
	}

	@Bean
	CustomerUpdateUseCase customerUpdateUseCase(CustomerUpdateGateway customerUpdateGateway){
		return new DefaultCustomerUpdateUseCase(customerUpdateGateway);
	}

	@Bean
	CustomerDeleteGateway customerDeleteGateway(CustomerJpaRepository customerJpaRepository,
	                                            CustomerEmailJpaRepository customerEmailJpaRepository,
	                                            CustomerPhoneJpaRepository customerPhoneJpaRepository,
	                                            CustomerAddressJpaRepository customerAddressJpaRepository,
	                                            TenantJpaRepository tenantJpaRepository){
		return new CustomerDeleteJpaGateway(customerJpaRepository, customerEmailJpaRepository,
				customerPhoneJpaRepository, customerAddressJpaRepository, tenantJpaRepository);
	}

	@Bean
	CustomerDeleteUseCase customerDeleteUseCase(CustomerDeleteGateway customerDeleteGateway){
		return new DefaultCustomerDeleteUseCase(customerDeleteGateway);
	}

	@Bean
	CustomerVerificationGateway customerVerificationGateway(CustomerJpaRepository customerJpaRepository){
		return new CustomerVerificationJpaGateway(customerJpaRepository);
	}

	@Bean
	CustomerVerificationUseCase customerVerificationUseCase(CustomerVerificationGateway customerVerificationGateway){
		return new DefaultCustomerVerificationUseCase(customerVerificationGateway);
	}

	@Bean
	TenantRegistrationGateway tenantRegistrationGateway(IdGenerator idGenerator,
	                                                    TenantJpaRepository tenantJpaRepository,
	                                                    TenantStatusJpaRepository tenantStatusJpaRepository,
	                                                    StaffJpaRepository staffJpaRepository,
	                                                    StaffRegistrationUseCase staffRegistrationUseCase){
		return new TenantRegistrationJpaGateway(idGenerator, tenantJpaRepository, tenantStatusJpaRepository,
				staffJpaRepository, staffRegistrationUseCase);
	}

	@Bean
	UserEmailPublisher tenantRegistrationEmailGateway(EmailTriggerJpaRepository emailTriggerJpaRepository,
	                                                  EmailTriggerTypeJpaRepository emailTriggerTypeJpaRepository,
	                                                  EmailTriggerStatusJpaRepository emailTriggerStatusJpaRepository,
	                                                  IdGenerator idGenerator, JsonManager jsonManager,
	                                                  CryptoTool cryptoTool,
	                                                  StringRedisTemplate stringRedisTemplate,
	                                                  PlatformTransactionManager transactionManager,
	                                                  @Value("${app.notification.stream.email.key}") String streamEmailKey){
		return new UserEmailRedisPublisher(emailTriggerJpaRepository, emailTriggerTypeJpaRepository,
				emailTriggerStatusJpaRepository, idGenerator, jsonManager, cryptoTool, stringRedisTemplate,
				transactionManager, streamEmailKey);
	}

	@Bean
	TenantRegistrationUseCase tenantRegistrationUseCase(TenantRegistrationGateway tenantRegistrationGateway,
	                                                    UserEmailPublisher emailPublisher,
	                                                    TurnstileVerificationGateway turnstileVerificationGateway,
	                                                    UserCacheManager userCacheManager){
		return new DefaultTenantRegistrationUseCase(tenantRegistrationGateway, emailPublisher,
				turnstileVerificationGateway, userCacheManager);
	}

	@Bean
	TenantConfirmRegistrationGateway tenantConfirmRegistrationGateway(TenantJpaRepository tenantJpaRepository,
	                                                                  TenantStatusJpaRepository tenantStatusJpaRepository){
		return new TenantConfirmRegistrationJpaGateway(tenantJpaRepository, tenantStatusJpaRepository);
	}

	@Bean
	TenantConfirmRegistrationUseCase tenantConfirmRegistrationUseCase(TenantConfirmRegistrationGateway tenantConfirmRegistrationGateway,
	                                                                  UserCacheManager userCacheManager){
		return new DefaultTenantConfirmRegistrationUseCase(tenantConfirmRegistrationGateway, userCacheManager);
	}

	@Bean
	TenantResendConfirmationGateway tenantResendConfirmationGateway(TenantJpaRepository tenantJpaRepository,
	                                                                StaffEmailJpaRepository staffEmailJpaRepository,
	                                                                CryptoTool cryptoTool){
		return new TenantResendConfirmationJpaGateway(tenantJpaRepository, staffEmailJpaRepository, cryptoTool);
	}

	@Bean
	TenantResendConfirmationUseCase tenantResendConfirmationUseCase(TenantResendConfirmationGateway tenantResendConfirmationGateway,
	                                                                UserEmailPublisher emailPublisher,
	                                                                UserCacheManager userCacheManager){
		return new DefaultTenantResendConfirmationUseCase(tenantResendConfirmationGateway, emailPublisher, userCacheManager);
	}

	@Bean
	TenantExpirationGateway tenantExpirationGateway(TenantJpaRepository tenantJpaRepository,
	                                                StaffJpaRepository staffJpaRepository,
	                                                PlatformTransactionManager transactionManager){
		return new TenantExpirationJpaGateway(tenantJpaRepository, staffJpaRepository, transactionManager);
	}

	@Bean
	TenantExpirationUseCase tenantExpirationUseCase(TenantExpirationGateway tenantExpirationGateway){
		return new DefaultTenantExpirationUseCase(tenantExpirationGateway);
	}

	@Bean
	TenantExpirationScheduler tenantExpirationScheduler(TenantExpirationUseCase tenantExpirationUseCase){
		TenantExpirationScheduler scheduler = new TenantExpirationScheduler(tenantExpirationUseCase);
		Thread.startVirtualThread(scheduler::expirePendingTenants);
		return scheduler;
	}

	@Bean
	TurnstileVerificationGateway turnstileVerificationGateway(JsonManager jsonManager,
	                                                          @Value("${app.turnstile.secret-key}") String secretKey,
	                                                          @Value("${app.turnstile.verify-url}") String verifyUrl){
		return new CloudflareTurnstileGateway(jsonManager, secretKey, verifyUrl);
	}

	@Bean
	StaffRegistrationGateway staffRegistrationGateway(StaffJpaRepository staffJpaRepository,
	                                                  StaffPasswordJpaRepository staffPasswordJpaRepository,
	                                                  StaffEmailJpaRepository staffEmailJpaRepository,
	                                                  StaffAddressJpaRepository staffAddressJpaRepository,
	                                                  StaffPhoneJpaRepository staffPhoneJpaRepository,
													  TenantJpaRepository tenantJpaRepository,
													  StaffRoleJpaRepository staffRoleJpaRepository,
	                                                  IdGenerator idGenerator,
	                                                  CryptoTool cryptoTool){
		return new StaffRegistrationJpaGateway(staffJpaRepository, staffPasswordJpaRepository, staffEmailJpaRepository,
				staffAddressJpaRepository, staffPhoneJpaRepository, staffRoleJpaRepository, tenantJpaRepository,
				idGenerator, cryptoTool);
	}

	@Bean
	StaffRegistrationUseCase staffRegistrationUseCase(StaffRegistrationGateway staffRegistrationGateway,
	                                                  PasswordTool passwordTool){
		return new DefaultStaffRegistrationUseCase(staffRegistrationGateway, passwordTool);
	}

	@Bean
	PasswordTool passwordTool(){
		Argon2Password4jPasswordEncoder passwordEncoder = new Argon2Password4jPasswordEncoder(
				Argon2Function.getInstance(15360, 2, 1, 32, Argon2.ID)
		);
		return new Argon2PasswordTool(passwordEncoder);
	}

	@Bean
	IdGenerator idGenerator(){
		return new UlidGenerator();
	}

	@Bean
	StaffLoginUseCase staffLoginUseCase(StaffLoginGateway staffLoginGateway, PasswordTool passwordTool,
	                                    TokenProcessor tokenProcessor, UserCacheManager userCacheManager){
		return new DefaultStaffLoginUseCase(staffLoginGateway, passwordTool, tokenProcessor, userCacheManager);
	}

	@Bean
	StaffLoginGateway staffLoginGateway(StaffJpaRepository staffJpaRepository,
	                                    UserSessionJpaRepository userSessionJpaRepository,
	                                    UserSessionTypeJpaRepository userSessionTypeJpaRepository,
	                                    TenantJpaRepository tenantJpaRepository){
		return new StaffLoginJpaGateway(staffJpaRepository, userSessionJpaRepository,
				userSessionTypeJpaRepository, tenantJpaRepository);
	}

	@Bean
	JsonManager jsonManager(ObjectMapper objectMapper){
		return new DefaultJsonManager(objectMapper);
	}

	@Bean
	CacheHandler cacheHandler(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper){
		return new DefaultCacheHandler(stringRedisTemplate, objectMapper);
	}

	@Bean
	UserCacheManager userCacheManager(CacheHandler cacheHandler, JsonManager jsonManager){
		return new DefaultUserCacheManager(cacheHandler, jsonManager);
	}

	@Bean
	StaffDetailGateway staffDetailGateway(StaffJpaRepository staffJpaRepository,
	                                      StaffEmailJpaRepository staffEmailJpaRepository,
	                                      StaffPhoneJpaRepository staffPhoneJpaRepository,
	                                      StaffAddressJpaRepository staffAddressJpaRepository,
	                                      CryptoTool cryptoTool){
		return new StaffDetailJpaGateway(staffJpaRepository,  staffEmailJpaRepository, staffPhoneJpaRepository,
				staffAddressJpaRepository, cryptoTool);
	}

	@Bean
	StaffDetailUseCase staffDetailUseCase(StaffDetailGateway staffDetailGateway){
		return new DefaultStaffDetailUseCase(staffDetailGateway);
	}

	@Bean
	StaffListGateway staffListGateway(StaffJpaRepository staffJpaRepository,
	                                  StaffEmailJpaRepository staffEmailJpaRepository,
	                                  StaffPhoneJpaRepository staffPhoneJpaRepository,
	                                  StaffAddressJpaRepository staffAddressJpaRepository,
	                                  CryptoTool cryptoTool){
		return new StaffListJpaGateway(staffJpaRepository, staffEmailJpaRepository, staffPhoneJpaRepository,
				staffAddressJpaRepository, cryptoTool);
	}

	@Bean
	StaffListUseCase staffListUseCase(StaffListGateway staffListGateway){
		return new DefaultStaffListUseCase(staffListGateway);
	}

	@Bean
	StaffRefreshTokenGateway staffRefreshTokenGateway(StaffJpaRepository staffJpaRepository,
	                                                  UserSessionJpaRepository userSessionJpaRepository,
													  UserSessionTypeJpaRepository userSessionTypeJpaRepository,
	                                                  TenantJpaRepository tenantJpaRepository){
		return new StaffRefreshTokenJpaGateway(staffJpaRepository, userSessionJpaRepository,
				userSessionTypeJpaRepository, tenantJpaRepository);
	}

	@Bean
	StaffRefreshTokenUseCase staffRefreshTokenUseCase(StaffRefreshTokenGateway staffRefreshTokenGateway,
	                                                  TokenProcessor tokenProcessor, UserCacheManager userCacheManager){
		return new DefaultStaffRefreshTokenUseCase(staffRefreshTokenGateway, tokenProcessor, userCacheManager);
	}

	@Bean
	StaffLogoutGateway staffLogoutGateway(UserSessionJpaRepository userSessionJpaRepository,
	                                      UserSessionTypeJpaRepository userSessionTypeJpaRepository){
		return new StaffLogoutJpaGateway(userSessionJpaRepository, userSessionTypeJpaRepository);
	}

	@Bean
	StaffLogoutUseCase staffLogoutUseCase(StaffLogoutGateway staffLogoutGateway, TokenProcessor tokenProcessor,
	                                      UserCacheManager userCacheManager){
		return new DefaultStaffLogoutUseCase(staffLogoutGateway, tokenProcessor, userCacheManager);
	}

	@Bean
	StaffForgottenPasswordGateway staffForgottenPasswordGateway(StaffEmailJpaRepository staffEmailJpaRepository,
	                                                            CryptoTool cryptoTool){
		return new StaffForgottenPasswordJpaGateway(staffEmailJpaRepository, cryptoTool);
	}

	@Bean
	StaffForgottenPasswordUseCase staffForgottenPasswordUseCase(StaffForgottenPasswordGateway staffForgottenPasswordGateway,
	                                                            UserEmailPublisher emailPublisher,
	                                                            UserCacheManager userCacheManager){
		return new DefaultStaffForgottenPasswordUseCase(staffForgottenPasswordGateway, emailPublisher, userCacheManager);
	}

	@Bean
	StaffSubmitOtpUseCase staffSubmitOtpUseCase(UserCacheManager userCacheManager){
		return new DefaultStaffSubmitOtpUseCase(userCacheManager);
	}

	@Bean
	StaffResetPasswordGateway staffResetPasswordGateway(StaffJpaRepository staffJpaRepository,
	                                                    StaffPasswordJpaRepository staffPasswordJpaRepository,
	                                                    IdGenerator idGenerator){
		return new StaffResetPasswordJpaGateway(staffJpaRepository, staffPasswordJpaRepository, idGenerator);
	}

	@Bean
	StaffResetPasswordUseCase staffResetPasswordUseCase(StaffResetPasswordGateway staffResetPasswordGateway,
	                                                    PasswordTool passwordTool, UserCacheManager userCacheManager){
		return new DefaultStaffResetPasswordUseCase(staffResetPasswordGateway, passwordTool, userCacheManager);
	}

	@Bean
	StaffDeleteGateway staffDeleteGateway(StaffJpaRepository staffJpaRepository,
	                                      StaffRoleJpaRepository staffRoleJpaRepository,
	                                      TenantJpaRepository tenantJpaRepository){
		return new StaffDeleteJpaGateway(staffJpaRepository, staffRoleJpaRepository, tenantJpaRepository);
	}

	@Bean
	StaffDeleteUseCase staffDeleteUseCase(StaffDeleteGateway staffDeleteGateway){
		return new DefaultStaffDeleteUseCase(staffDeleteGateway);
	}

	@Bean
	StaffUpdateGateway staffUpdateGateway(StaffJpaRepository staffJpaRepository,
	                                      StaffPasswordJpaRepository staffPasswordJpaRepository,
	                                      StaffRoleJpaRepository staffRoleJpaRepository,
	                                      StaffEmailJpaRepository staffEmailJpaRepository,
	                                      StaffPhoneJpaRepository staffPhoneJpaRepository,
	                                      StaffAddressJpaRepository staffAddressJpaRepository,
	                                      TenantJpaRepository tenantJpaRepository,
	                                      IdGenerator idGenerator,
	                                      CryptoTool cryptoTool){
		return new StaffUpdateJpaGateway(staffJpaRepository, staffPasswordJpaRepository, staffRoleJpaRepository,
				staffEmailJpaRepository, staffPhoneJpaRepository, staffAddressJpaRepository, tenantJpaRepository,
				idGenerator, cryptoTool);
	}

	@Bean
	StaffUpdateUseCase staffUpdateUseCase(StaffUpdateGateway staffUpdateGateway, PasswordTool passwordTool){
		return new DefaultStaffUpdateUseCase(staffUpdateGateway, passwordTool);
	}

}
