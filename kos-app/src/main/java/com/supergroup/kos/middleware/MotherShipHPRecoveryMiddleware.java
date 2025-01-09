//package com.supergroup.kos.middleware;
//
//import java.io.IOException;
//
//import javax.persistence.EntityManagerFactory;
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.hibernate.FlushMode;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.springframework.orm.hibernate5.SessionFactoryUtils;
//import org.springframework.orm.hibernate5.SessionHolder;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.support.TransactionSynchronizationManager;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import com.supergroup.kos.building.domain.command.UserCommand;
//import com.supergroup.kos.building.domain.repository.persistence.ship.MotherShipRepository;
//import com.supergroup.kos.building.domain.service.profile.KosProfileService;
//import com.supergroup.kos.config.AccessSession;
//import com.supergroup.kos.util.AuthUtil;
//
//import lombok.RequiredArgsConstructor;
//
///**
// * @author idev
// * This filter valdiate current mothership hp by calculating hp recovery
// * for all user's mother ship
// */
//@Component
//@RequiredArgsConstructor
//public class MotherShipHPRecoveryMiddleware extends OncePerRequestFilter {
//
//    private final MotherShipRepository motherShipRepository;
//    private final KosProfileService    kosProfileService;
//    private final EntityManagerFactory entityManagerFactory;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        // Open hibernate session
//        // WARNING: I am not aware danger this code cause
//        var sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
//        Session session = sessionFactory.openSession();
//        session.setHibernateFlushMode(FlushMode.MANUAL);
//        var sessionHolder = new SessionHolder(session);
//        TransactionSynchronizationManager.bindResource(sessionFactory, sessionHolder);
//        try {
//            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof AccessSession) {
//
//                var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
//                var motherShips = motherShipRepository.findByKosProfileId(kosProfile.getId());
//                for (var motherShip : motherShips) {
//                    motherShip.validateCurrentHp();
//                    motherShipRepository.updateForRecoveryHp(motherShip.getId(), motherShip.getCurrentHp(), motherShip.getArrivalMainBaseTime(),
//                                                             motherShip.getLastTimeCalculateHp());
//                }
//            }
//        } catch (Exception exception) {
//            exception.printStackTrace();
//            // ignore
//        }
//        filterChain.doFilter(request, response);
//        // close hibernate session
//        // WARNING: I am not aware effect this code cause
//        sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
//        SessionFactoryUtils.closeSession(sessionHolder.getSession());
//    }
//}
