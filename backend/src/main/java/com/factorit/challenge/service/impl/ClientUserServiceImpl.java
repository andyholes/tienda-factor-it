package com.factorit.challenge.service.impl;

import com.factorit.challenge.model.ClientUser;
import com.factorit.challenge.model.payload.ClientUserResponse;
import com.factorit.challenge.repository.ClientUserRepository;
import com.factorit.challenge.service.ClientUserService;
import com.factorit.challenge.utils.EntityMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.factorit.challenge.utils.MessageConstants.USER_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientUserServiceImpl implements ClientUserService {
    private final ClientUserRepository userRepository;
    private final EntityMapper mapper;

    @Override
    public List<ClientUserResponse> getAll(Boolean vip, YearMonth monthOfUpdate){
        return userRepository.findAll(setFilters(vip, monthOfUpdate))
                .stream().map(mapper::toDto).toList();
    }

    @Override
    public ClientUserResponse getById(Long id){
        return mapper.toDto(getUserEntity(id));
    }

    private ClientUser getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
    }

    private Specification<ClientUser> setFilters(Boolean vip, YearMonth updateDate){
        return ((root, query, criteriaBuilder) -> {
         List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(vip).ifPresent(value ->
                predicates.add(criteriaBuilder.equal(root.get("vip"), value)));

        Optional.ofNullable(updateDate).ifPresent(date ->
                predicates.add(criteriaBuilder.equal(root.get("vipUpdateDate"), date)));

         return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

//Aca deberia usar @Scheduled(cron="@monthly") para ejecutarlo al comienzo de cada mes
//pero lo configure cada 10 minutos asi se puede ver como funciona
@Scheduled(cron = "0 */10 * * * *")
    private void removeVipFromInactiveUsers(){
        List<ClientUser> vipUsers = userRepository.findAll(setFilters(true,null));
        vipUsers.stream()
                .filter(user -> user.getLastPurchaseDate()
                        .isBefore(LocalDate.now().minusMonths(1)))
                .toList().forEach(user -> {
                    user.setVip(false);
                    user.setVipUpdateDate(YearMonth.now());
                    userRepository.save(user);
                    String fullName = user.getName() + " "+ user.getLastName();
                    log.info(fullName +" is no longer a vip client");
                });
    }
}
