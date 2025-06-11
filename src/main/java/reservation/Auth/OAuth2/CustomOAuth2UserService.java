package reservation.Auth.OAuth2;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import reservation.Entity.User;
import reservation.Entity.User.Role;
import reservation.Enum.AuthProvider;
import reservation.Enum.ErrorCode;
import reservation.Exception.UserException;
import reservation.Repository.UserRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//		Assert.notNull(userRequest, "userRequest cannot be null");
//		String userNameAttributeName = getUserNameAttributeName(userRequest);
//		RequestEntity<?> request = this.requestEntityConverter.convert(userRequest);
//		ResponseEntity<Map<String, Object>> response = getResponse(userRequest, request);
//		OAuth2AccessToken token = userRequest.getAccessToken();
//		Map<String, Object> attributes = this.attributesConverter.convert(userRequest).convert(response.getBody());
//		Collection<GrantedAuthority> authorities = getAuthorities(token, attributes, userNameAttributeName);
//		return new DefaultOAuth2User(authorities, attributes, userNameAttributeName);

		System.out.println("OAUTH2: inside custom handler");
		OAuth2User oAuth2User = super.loadUser(userRequest);

		String email = oAuth2User.getAttribute("email");
		String providerId = oAuth2User.getAttribute("sub"); // google code
		Optional<User> user = userRepository.findByEmail(email);

		if (user.isPresent()) {
			User currentUser = user.get();
			if (!currentUser.getProvider().equals(AuthProvider.GOOGLE)
					|| !currentUser.getProviderId().equals(providerId)) {
				//new OAuth2AuthenticationException(OAuth2Error error)
				throw new OAuth2AuthenticationException(new OAuth2Error("email_already_registered",
						"Email is already registered with " + currentUser.getProvider(), null));
			}
		} else {
//			String username = email.split("@")[0] + "_google";
			String dummyPassword = passwordEncoder.encode(UUID.randomUUID().toString());

			User newUser = new User();
			newUser.setUsername(email);
			newUser.setEmail(email);
			newUser.setPassword(dummyPassword);
			newUser.setProvider(AuthProvider.GOOGLE);
			newUser.setProviderId(providerId);
			newUser.setRole(Role.GUEST);
			userRepository.save(newUser);
			System.out.println("OAUTH2: user is saved");
		}
		return oAuth2User;
	}
}

/*public class OAuth2Error {
    private final String errorCode;
    private final String description;
    private final String uri;
}
 */