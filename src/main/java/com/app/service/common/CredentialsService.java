package com.app.service.common;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.app.controller.common.Routes;
import com.app.entities.administration.Assignation;
import com.app.entities.administration.Utilisateur;
import com.app.entities.referentiel.Exercice;
import com.app.security.UserPrincipal;
import com.app.service.administration.AssignationService;
//import com.app.service.administration.MenuService;
import com.app.service.administration.UtilisateurService;
import com.app.service.referentiel.ExerciceService;
import com.app.utils.Constants;
import com.app.utils.JUtils;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CredentialsService {

 //   private final MenuService menuService;
    private final AssignationService assignationService;
    private final UtilisateurService utilisateurService;
    private final HttpSession httpSession;
    private final ExerciceService exerciceService;

    /***** */

    /**
     * User principal get from below class
     * 
     * @link ci.gouv.feuille_route.service.security.CustomUserDetailsService}
     */
    public String loggedUserRoleHandler(HttpServletRequest request) {
        String redirectLink = Routes.REDIRECT_HOME;
        UserPrincipal userPrincipal = getUserPrincipalSession();
        userPrincipal.setLoggedIn(true);
        Utilisateur utilisateur = userPrincipal.getUtilisateur();
        List<Assignation> assignations = utilisateur.getAssignations();
        // racourcir from user
        userPrincipal.setAssignations(assignations);
        userPrincipal.hasRole(!assignations.isEmpty());
        updateLoggedInfo(request);
        // cas spécial
		/*
		 * if (userPrincipal.isDefaultUser()) {
		 * userPrincipal.getUtilisateur().setResetPwd(false); redirectLink =
		 * Routes.REDIRECT_SELECTION_ROLE_URL; } else if (assignations.size() == 1) {
		 * userPrincipal.hasMultipleRole(false); roleSelectedHandler(userPrincipal,
		 * assignations.get(0), true); } else if (assignations.size() > 1) {
		 * userPrincipal.hasMultipleRole(true);
		 * userPrincipal.setAssignationCourant(assignations.get(0));
		 * Comparator<Assignation> assignCompare = Comparator.comparing(assgn ->
		 * assgn.getRole().getLibelle()); Collections.sort(assignations, assignCompare);
		 * redirectLink = Routes.REDIRECT_SELECTION_ROLE_URL; } if
		 * (!userPrincipal.isDefaultUser() && userPrincipal.isActeurObservateur()) {
		 * redirectLink = Routes.REDIRECT_SELECTION_VOLET_URL; }
		 */

        setUserPrincipalSession(userPrincipal);

        return redirectLink;
    }

  //  public void roleSelectedHandlerFromChoix(UserPrincipal userPrincipal, int assignIdSelected) {
    //    setUserPrincipalSession(roleSelectedHandler(userPrincipal, assignIdSelected));
 //   }

    //public UserPrincipal roleSelectedHandler(UserPrincipal userPrincipal, int assignIdSelected) {
      //  Optional<Assignation> assignation = (assignIdSelected == -1) ? Optional.empty()
      //          : assignationService.findById(assignIdSelected);
      //  return roleSelectedHandler(userPrincipal, assignation.orElse(null), true);
    //}

    private UserPrincipal roleSelectedHandler(UserPrincipal userPrincipal, Assignation assignationCourant,
            boolean bool) {
        loggedUserWithRoleAccessHandler(userPrincipal, assignationCourant);
        extraUserDataHandler(userPrincipal);
        return userPrincipal;
    }

    private void extraUserDataHandler(UserPrincipal userPrincipal) {
        List<Exercice> listExoOpen = exerciceService.findByStatutCode(Constants.STATUT_OUVERTURE_CODE);

        if (listExoOpen != null && !listExoOpen.isEmpty()) {
            userPrincipal.setExercice(listExoOpen.get(0));
            userPrincipal.setExercices(listExoOpen);
        }

    }

    private void loggedUserWithRoleAccessHandler(UserPrincipal userPrincipal, Assignation assignationCourant) {
        if (assignationCourant != null && assignationCourant.getId() > 0) {
            userPrincipal.setAssignationCourant(assignationCourant);
            var typeRole = assignationCourant.getRole().getTypeRole();
 //           menuService.getMenuTreeDataByTypeRole(typeRole, userPrincipal);
        } else if (userPrincipal.isDefaultUser()) {
            userPrincipal.hasMultipleRole(true);
            userPrincipal.hasRole(true);

 //           userPrincipal.setMenus(menuService.getMenuDefaultUser());
        }
    }

    private void setUserPrincipalSession(UserPrincipal userConnected) {
        httpSession.setAttribute(Constants.APP_CREDENTIALS, userConnected);
    }

    private UserPrincipal getUserPrincipalSession() {
        return (UserPrincipal) httpSession.getAttribute(Constants.APP_CREDENTIALS);
    }

    public void updateLoggedInfo(HttpServletRequest request) {
        UserPrincipal userCourant = getUserPrincipalSession();
        final Utilisateur utilisateur = userCourant.getUtilisateur();
        if (!userCourant.getUtilisateur().isDefaultUser()) {
            utilisateur.setLastConnexionDate(LocalDateTime.now());
            utilisateur.setIpConnexion(JUtils.getIpAdresseInfo(request));
            utilisateurService.update(utilisateur);
        }

    }

    @SuppressWarnings("unused")
    private void clearAllCache() {
    }

}
