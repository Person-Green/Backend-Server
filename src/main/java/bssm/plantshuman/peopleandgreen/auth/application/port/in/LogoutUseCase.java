package bssm.plantshuman.peopleandgreen.auth.application.port.in;

public interface LogoutUseCase {

    void logout(String refreshToken);
}
