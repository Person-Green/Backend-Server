package bssm.plantshuman.peopleandgreen.auth.application.port.out;

public interface RefreshTokenHasherPort {

    String hash(String token);
}
