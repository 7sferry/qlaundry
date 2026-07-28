package com.ferry.user.webservice.config;

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
import com.ferry.user.core.tenant.registration.DefaultTenantRegistrationUseCase;
import com.ferry.user.core.tenant.registration.UserEmailPublisher;
import com.ferry.user.core.tenant.registration.TenantRegistrationGateway;
import com.ferry.user.core.tenant.registration.TenantRegistrationUseCase;
import com.ferry.user.core.tenant.registration.TurnstileVerificationGateway;
import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.core.tools.TokenProcessor;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.gateway.notification.UserEmailRedisPublisher;
import com.ferry.user.gateway.notification.repository.EmailTriggerJpaRepository;
import com.ferry.user.gateway.session.repository.UserSessionJpaRepository;
import com.ferry.user.gateway.session.repository.UserSessionTypeJpaRepository;
import com.ferry.user.gateway.staff.*;
import com.ferry.user.gateway.staff.repository.*;
import com.ferry.user.gateway.tenant.CloudflareTurnstileGateway;
import com.ferry.user.gateway.tenant.TenantRegistrationJpaGateway;
import com.ferry.user.gateway.tenant.repository.TenantJpaRepository;
import com.ferry.user.webservice.tools.Argon2PasswordTool;
import com.ferry.user.webservice.tools.DefaultUserCacheManager;
import com.ferry.utils.cache.CacheHandler;
import com.ferry.utils.cache.DefaultCacheHandler;
import com.ferry.utils.generator.IdGenerator;
import com.ferry.utils.generator.UlidGenerator;
import com.ferry.utils.json.DefaultJsonManager;
import com.ferry.utils.json.JsonManager;
import com.password4j.Argon2Function;
import com.password4j.types.Argon2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password4j.Argon2Password4jPasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import tools.jackson.databind.ObjectMapper;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Configuration
public class UserWebConfig{

	@Bean
	TenantRegistrationGateway tenantRegistrationGateway(IdGenerator idGenerator,
	                                                    TenantJpaRepository tenantJpaRepository,
	                                                    StaffRegistrationUseCase staffRegistrationUseCase){
		return new TenantRegistrationJpaGateway(idGenerator, tenantJpaRepository, staffRegistrationUseCase);
	}

	@Bean
	UserEmailPublisher tenantRegistrationEmailGateway(EmailTriggerJpaRepository emailTriggerJpaRepository,
	                                                  IdGenerator idGenerator, JsonManager jsonManager,
	                                                  StringRedisTemplate stringRedisTemplate,
	                                                  PlatformTransactionManager transactionManager,
	                                                  @Value("${app.notification.stream.email.key}") String streamEmailKey){
		return new UserEmailRedisPublisher(emailTriggerJpaRepository, idGenerator, jsonManager, stringRedisTemplate,
				transactionManager, streamEmailKey);
	}

	@Bean
	TenantRegistrationUseCase tenantRegistrationUseCase(TenantRegistrationGateway tenantRegistrationGateway,
	                                                    UserEmailPublisher emailPublisher,
	                                                    TurnstileVerificationGateway turnstileVerificationGateway){
		return new DefaultTenantRegistrationUseCase(tenantRegistrationGateway, emailPublisher, turnstileVerificationGateway);
	}

	@Bean
	TurnstileVerificationGateway turnstileVerificationGateway(JsonManager jsonManager,
	                                                          @Value("${app.turnstile.secret-key}") String secretKey,
	                                                          @Value("${app.turnstile.verify-url}") String verifyUrl){
		return new CloudflareTurnstileGateway(jsonManager, secretKey, verifyUrl);
	}

	@Bean
	StaffRegistrationGateway staffRegistrationGateway(StaffJpaRepository staffJpaRepository,
	                                                  StaffEmailJpaRepository staffEmailJpaRepository,
	                                                  StaffAddressJpaRepository staffAddressJpaRepository,
	                                                  StaffPhoneJpaRepository staffPhoneJpaRepository,
													  TenantJpaRepository tenantJpaRepository,
													  StaffRoleJpaRepository staffRoleJpaRepository,
	                                                  IdGenerator idGenerator){
		return new StaffRegistrationJpaGateway(staffJpaRepository, staffEmailJpaRepository, staffAddressJpaRepository,
				staffPhoneJpaRepository, staffRoleJpaRepository, tenantJpaRepository, idGenerator);
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
		return new StaffLoginJpaGateway(staffJpaRepository, userSessionJpaRepository, userSessionTypeJpaRepository,
				tenantJpaRepository);
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
	                                      StaffAddressJpaRepository staffAddressJpaRepository){
		return new StaffDetailJpaGateway(staffJpaRepository,  staffEmailJpaRepository, staffPhoneJpaRepository, staffAddressJpaRepository);
	}

	@Bean
	StaffDetailUseCase staffDetailUseCase(StaffDetailGateway staffDetailGateway){
		return new DefaultStaffDetailUseCase(staffDetailGateway);
	}

	@Bean
	StaffListGateway staffListGateway(StaffJpaRepository staffJpaRepository,
	                                  StaffEmailJpaRepository staffEmailJpaRepository,
	                                  StaffPhoneJpaRepository staffPhoneJpaRepository,
	                                  StaffAddressJpaRepository staffAddressJpaRepository){
		return new StaffListJpaGateway(staffJpaRepository, staffEmailJpaRepository, staffPhoneJpaRepository, staffAddressJpaRepository);
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
	StaffForgottenPasswordGateway staffForgottenPasswordGateway(StaffEmailJpaRepository staffEmailJpaRepository){
		return new StaffForgottenPasswordJpaGateway(staffEmailJpaRepository);
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
	                                                    StaffRoleJpaRepository staffRoleJpaRepository,
	                                                    TenantJpaRepository tenantJpaRepository){
		return new StaffResetPasswordJpaGateway(staffJpaRepository, staffRoleJpaRepository, tenantJpaRepository);
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
	                                      StaffRoleJpaRepository staffRoleJpaRepository,
	                                      StaffEmailJpaRepository staffEmailJpaRepository,
	                                      StaffPhoneJpaRepository staffPhoneJpaRepository,
	                                      StaffAddressJpaRepository staffAddressJpaRepository,
	                                      TenantJpaRepository tenantJpaRepository,
	                                      IdGenerator idGenerator){
		return new StaffUpdateJpaGateway(staffJpaRepository, staffRoleJpaRepository, staffEmailJpaRepository,
				staffPhoneJpaRepository, staffAddressJpaRepository, tenantJpaRepository, idGenerator);
	}

	@Bean
	StaffUpdateUseCase staffUpdateUseCase(StaffUpdateGateway staffUpdateGateway, PasswordTool passwordTool){
		return new DefaultStaffUpdateUseCase(staffUpdateGateway, passwordTool);
	}

}
