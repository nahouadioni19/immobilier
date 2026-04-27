/*
 * package com.app.service.administration;
 * 
 * import java.util.ArrayList; import java.util.Collections; import
 * java.util.Comparator; import java.util.HashMap; import java.util.List; import
 * java.util.Map; import java.util.Map.Entry; import java.util.Optional; import
 * java.util.function.Function; import java.util.stream.Collectors;
 * 
 * import org.apache.commons.lang3.math.NumberUtils; import
 * org.springframework.data.jpa.repository.JpaRepository; import
 * org.springframework.stereotype.Service; import
 * org.springframework.transaction.annotation.Transactional;
 * 
 * import com.app.enums.ActionMenu; import com.app.exceptions.CustomException;
 * import com.app.entities.administration.DroitAcces; import
 * com.app.entities.administration.Menu; import
 * com.app.entities.administration.Typerole; import
 * com.app.repositories.administration.MenuRepository; import
 * com.app.security.UserPrincipal; import com.app.service.base.BaseService;
 * import com.app.utils.Constants; import com.app.utils.JUtils; import
 * lombok.RequiredArgsConstructor;
 * 
 * @Service
 * 
 * @Transactional
 * 
 * @RequiredArgsConstructor public class MenuService extends BaseService<Menu> {
 * 
 * public static final String MENU_TREE = "MENU_TREE"; public static final
 * String MENU_ACTIONS = "MENU_ACTIONS"; private final MenuRepository repo;
 * private final TyperoleService typeRoleService;
 * 
 * @Override public JpaRepository<Menu, Integer> getRepository() { return repo;
 * }
 * 
 * public List<Menu> findUpTree(int id) { return repo.findUpTree(id); }
 * 
 * public String getMenuUK(String href) { String menuUK = "";
 * 
 * if (Constants.SEPARATEUR_URL.equals(href)) { menuUK =
 * Constants.SEPARATEUR_URL; } else { if
 * (href.startsWith(Constants.SEPARATEUR_URL)) href = href.substring(1);
 * 
 * menuUK = href.replace(Constants.SEPARATEUR_URL, "_"); }
 * 
 * return menuUK; }
 * 
 * @Transactional(readOnly = false) public List<Menu> findByParent(Menu parent)
 * { List<Menu> res = repo.findByParentOrderByOrdre(parent);
 * 
 * if ((res != null) && !res.isEmpty()) { res.forEach(mnu ->
 * mnu.setChildren(findByParent(mnu))); }
 * 
 * return res; }
 * 
 * @Transactional(readOnly = false) public List<Menu> getMenus() { return
 * findByParent(null); }
 * 
 * @Transactional(readOnly = false) public String getMenusAsJson(List<Menu>
 * list) { return JUtils.toJSON(list); }
 * 
 *//**
	 * method appelé uniquement par
	 * CredentialsService#loggedUserWithRoleAccessHandler
	 * 
	 * @param typeRole
	 * @return
	 *//*
		 * @Transactional(readOnly = false) public void
		 * getMenuTreeDataByTypeRole(Typerole typeRole, UserPrincipal userPrincipal) {
		 * Map<String, List<String>> menuKeyToActions = new HashMap<>(); List<Menu>
		 * listMenu = repo.findUpTreeByTypeRole(typeRole.getId()); Map<String, Menu>
		 * mapMenu = listMenu.stream().collect(Collectors.toMap(Menu::getCode,
		 * Function.identity()));
		 * 
		 * typeRole.getDroits().stream().forEach(dr -> { final String code =
		 * dr.getMenu().getCode(); var actions = mapMenu.get(code).getActions();
		 * actions.add(dr.getAction().name()); menuKeyToActions.put(code, actions); });
		 * userPrincipal.setMenus(buildMenuUpTree(listMenu, mapMenu));
		 * userPrincipal.setMenuKeyToActions(menuKeyToActions); }
		 * 
		 * private List<Menu> buildMenuUpTree(List<Menu> listMenuWithAccess, Map<String,
		 * Menu> mapMenu) { listMenuWithAccess.stream().forEach(menu -> { if
		 * (menu.getParent() != null && menu.getParent().getId() > 0) { final Menu
		 * parent = mapMenu.get(menu.getParent().getCode());
		 * parent.addToChildList(menu); Collections.sort(parent.getChildList(),
		 * Comparator.comparing(Menu::getOrdre)); } });
		 * 
		 * List<Menu> finalList = mapMenu.entrySet().stream() .filter(entry ->
		 * entry.getValue().getParent() == null || entry.getValue().getParent().getId()
		 * == 0) .map(Entry::getValue).collect(Collectors.toList());
		 * Collections.sort(finalList, Comparator.comparing(Menu::getOrdre)); return
		 * finalList; }
		 * 
		 * @Transactional(readOnly = false) public List<Menu> getMenuDefaultUser() { var
		 * listMenu = findAll(); Map<String, Menu> mapMenu =
		 * listMenu.stream().collect(Collectors.toMap(Menu::getCode,
		 * Function.identity())); return buildMenuUpTree(listMenu, mapMenu); }
		 * 
		 * //
		 * 
		 * @SuppressWarnings("unchecked") public void saveTypeRole(Typerole typeRole,
		 * boolean isUpdate) { Map<String, List<String>> droitsMap =
		 * JUtils.fromJSON(typeRole.getJson(), Map.class); if (isUpdate) { String
		 * libelle = typeRole.getLibelle(); String code = typeRole.getCode(); typeRole =
		 * typeRoleService.findById(typeRole.getId()) .orElseThrow(() -> new
		 * CustomException("Type de role introuvable")); typeRole.setLibelle(libelle);
		 * typeRole.setCode(code); typeRole.getDroits().clear(); } else
		 * typeRole.setDroits(new ArrayList<>());
		 * 
		 * List<DroitAcces> droitsAcces = new ArrayList<>(); final Typerole type =
		 * typeRole; for (Entry<String, List<String>> entry : droitsMap.entrySet()) {
		 * int menuId = NumberUtils.toInt(entry.getKey(), 0); Optional<Menu> menu =
		 * findById(menuId); if (menu.isPresent()) { entry.getValue().forEach(action ->
		 * { ActionMenu actionMenu = ActionMenu.valueOf(action); if (actionMenu != null)
		 * { droitsAcces.add(new DroitAcces(type, menu.get(), actionMenu, null)); } });
		 * } }
		 * 
		 * typeRole.getDroits().addAll(droitsAcces);
		 * 
		 * if (isUpdate) { typeRoleService.update(typeRole); } else {
		 * typeRoleService.save(typeRole); } }
		 * 
		 * public TyperoleService getTypeRoleService() { return this.typeRoleService; }
		 * }
		 * 
		 */