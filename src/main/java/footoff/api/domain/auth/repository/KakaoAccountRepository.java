package footoff.api.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import footoff.api.domain.auth.entity.KakaoAccount;

@Repository
public interface KakaoAccountRepository extends JpaRepository<KakaoAccount, Long> {

}
