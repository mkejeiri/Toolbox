# Spring Security Core :  User Lockout with Spring Security Events


Spring Security Authentication Events
--------
- **Spring Security** will send an **Authentication Success** or **Authentication Failure** **Event** with every **Authentication Attempt**.
- These **events** are important **hooks** for **monitoring** **system access**.
- Can be used for:
	- **Logging** who **logged in** **when** and from **where**.
	- **Failed log** in attempts.
	- Automatically **lock accounts** after too **many attempts**.	
- **Spring Security Authentication Events** use the **Event functionality** found in Spring Framework.
- **Publish/Subscribe** Type of Model:
	- Spring Security will **publish the event**
	- **One or more listeners** can **register** to **receive** the **event**.
- **Available** in all Spring Security Provided **Authentication Providers** : We have to write our own **event publishing**, if using  a **custom provider**.	

Default Event Mappings Authentication Events
---------
- `BadCredentialsException` = `AuthenticationFailureBadCredentialsEvent`
- `UsernameNotFoundException` = `AuthenticationFailureBadCredentialsEvent`
- `AccountExpiredException` = `AuthenticationFailureExpiredEvent`
- `ProviderNotFoundException` = `AuthenticationFailureProviderNotFoundEvent`
- `DisabledException` = `AuthenticationFailureDisabledEvent`
- `LockedException` = `AuthenticationFailureLockedEvent`
- `AuthenticationServiceException` = `AuthenticationFailureServiceExceptionEvent`
- `CredentialsExpiredException` = `AuthenticationFailureCredentialsExpiredEvent`
- `InvalidBearerTokenException` = `AuthenticationFailureBadCredentialsEvent`


#### Customization of Events
**Spring Security can be configured** for additional or **custom Authentication Events**, we could provide our own instance of **AuthenticationEventPublisher**, and it could be **customized as needed**.