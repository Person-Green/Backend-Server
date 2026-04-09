package bssm.plantshuman.peopleandgreen.auth.application.port.out;

public interface RefreshTokenHashPort {

    String hash(String token);
}
