package com.app.utils;

import java.util.List;

public class Constants {
    private Constants() {
    }

    public static final String APP_SIGPID = "AGRO";
    public static final String APP_CREDENTIALS = "CREDENTIALS";
    // ##
    public static final String APP_NAME = "appName";
    public static final String APP_ACRONYM = "appAcronym";
    public static final String MENU_ITEMS = "menuItems";
    public static final String USER_PRINCIPAL = "userPrincipal";
    public static final String VIEW_PATH = "viewPath";
    public static final String VIEW_PATH_NO_CONTEXT = "viewPathNoContext";

    // app config
    public static final String COOKIES_NAME = "JSESSIONID";
    public static final int MAX_SESSION = 1;
    public static final String ROLE_APP_USER = "APPUSER";
    public static final String ROLE_DEV_USER = "DEVUSER";
    public static final String ROLE_SIMPLE_USER = "SIMPLE_USER";

    //
    public static final String NON_DEFINI = "N/A";
    public static final String BLANK_MESSAGE = "label.blank";

    // Fichiers
    public static final String TEMP_DIR_FICHIER = "tmpFiles";
    public static final String BASE_DIRECTORY = "files";
    public static final String BASE_FILE_DOSSIER = "dossier";
    public static final String PATH_SEPARATOR = "/"; // File.separator
    public static final String DIRECTORY_FORMAT = "%d" + PATH_SEPARATOR + "%s";
    public static final String REPORT_DIR = "reports/";
    public static final CharSequence FAKEPATH = "fakepath";

    // #region File
    public static final String UPLOAD_BASE_DIR = "files";
    public static final int UPLOAD_DIR_DEPTH = 3;
    public static final String UPLOAD_DIR = "files/%d/%s/";
    public static final String UPLOAD_DIR_ACTES = "actes";
    public static final String UPLOAD_DIR_PIECES_JUSTIF = "pieces_justificatives";
    // #endregion

    // Date utils
    public static final String DEFAULT_METHOD_SEPARATOR = "::";
    //public static final String FORMAT_DATE_DEFAULT = "dd/MM/yyyy"; yyyy-MM-dd
    public static final String FORMAT_DATE_DEFAULT = "yyyy-MM-dd";
    public static final String FORMAT_DATE_TIME_DEFAULT = "dd/MM/yyyy HH:mm:ss";
    public static final String FORMAT_DATE_TIME_ENG = "yyyy-MM-dd HH:mm:ss";

    // user
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_PF = "PF";
    public static final String ROLE_PRIM = "PRIM";
    public static final String ROLE_DEVELOPEUR = "SSADM";
    public static final String DEFAULT_USER_NAME = "admpg";
    public static final String DEFAULT_USER_PASS = "pg@2022";
    public static final String ROLE_RECOUV = "RECOUV";
    public static final String ROLE_DIREC = "DIREC";
    public static final String ROLE_SECRET = "SECRET";
    
    public static final String DEFAULT_PASSWORD = "0000";
 //   public static final String DEFAULT_PASSWORD = "$2a$10$P0bchvX1.P8Tr1kHpQOeVOh2PC9I0E/yOSmIUw77BwR7/2pxwhuKi";
    
 // exemple d’un hash BCrypt de "pg@2022"
    public static final String DEFAULT_USER_PASS_HASH = "$2a$10$7QnV.5kU2v4KcTnZQfG8.e9w/GLQZlz9yEih3C0p7M3rP3cZqXZ5y";

    public static final int NOMBRE_ELEMENTS_PAR_PAGE = 10;

    public static final String TYPE_ROLE_OBSERVATEUR = "OBS";

    // jasper header
    public static final String SHOW_IN_BROWSER = "SHOW_IN_BROWSER";
    public static final String DIRECT_DOWNLOAD = "DIRECT_DOWNLOAD";

    public static final String ETAT_RENDER_TYPE = "renderType";
    public static final String ETAT_FILE_NAME = "fileName";
    public static final String ETAT_FILE_TYPE = "fileType";

    //
    public static final String DELIMITER_SINGLE_LIGNE = "\n";
    public static final String DELIMITER_DOUBLE_LIGNE = "\n\n";
    public static final String DELIMITER_DOUBLE_POINT = ":";
    public static final String SEPARATEUR_URL = "/";
    public static final String DEFAULT_SEPARATOR = ",";

    // #region BaseController Constantes
    public static final String MODEL_ATTRIBUTE_ENTITIES = "entities";
    public static final String MODEL_ATTRIBUTE_ENTITY = "entity";
    public static final String MODEL_ATTRIBUTE_VIEW_ONLY = "viewOnly";
    public static final String MODEL_ATTRIBUTE_READ_ONLY = "readonly";
    public static final String MODEL_ATTRIBUTE_IS_UPDATE = "isUpdate";
    public static final String MODEL_ATTRIBUTE_IS_VIEW = "isView";
    public static final String MODEL_ATTRIBUTE_VIEW_MODE = "viewMode";
    public static final String MODEL_ATTRIBUTE_EDIT_MODE = "editMode";
    public static final String MODEL_ATTRIBUTE_OPERATION_SUCCESS = "operation_success";
    public static final String ACTION_POST_FORM = "actionPost";
    public static final String MODEL_ATTRIBUTE_FLASH = "FLASH";
    public static final String MODEL_ATTRIBUTE_AJAX_SUBMIT = "AJAX_SUBMIT";
    public static final String MODEL_ATTRIBUTE_AJAX_RESPONSE = "AJAX_RESPONSE";

    public static final String REQUEST_PARAM_APP_MODULE = "appModule";
    public static final String REQUEST_PARAM_APP_GROUPE = "appGroupe";
    public static final String REQUEST_PARAM_APP_MENU = "appMenu";
    public static final String REQUEST_PARAM_APP_ACTION = "appAction";
    public static final String REQUEST_PARAM_ENTITY_ID = "id";

    public static final String MODEL_ATTRIBUTE_FILE_MAX_SIZE = "fileMaxSize";
    public static final String MODEL_ATTRIBUTE_PATH_SEPARATOR = "pathSeparator";
    public static final String MODEL_ATTRIBUTE_UPLOADED_FILES = "UPLOADED_FILES";
    public static final String UPLOADED_FILES = "UPLOADED_FILES";

    // ## setup page
    public static final String OPERATION_SUCCESS = "OPERATION_SUCCESS";
    public static final String RETURN_LINK = "returnLink";
    public static final String RESULTAT_MESSAGE = "resultatMessage";
    public static final String MESSAGE_CONFIRMATION = "messageConfirmation";
    public static final String LABEL_BUTTON_ACTION = "labelButtonAction";

    public static final String ACTION = "action";

    public static final String PAGE_TITLE = "pageTitle";
    public static final String PAGE_TITLE_DEFAULT = "title.default";
    public static final String FEATURE_TITLE = "featureTitle";
    public static final String MODEL_ATTRIBUTE_SUCCESS = "SUCCESS";
    public static final String MODEL_ATTRIBUTE_NONE = "NONE";
    public static final String CURR_PAGE = "CURR_PAGE";
    public static final String GRP_PAGE = "GRP_PAGE";

    public static final String APP_ACTION_CREATE = "create";
    public static final String APP_ACTION_EDIT = "edit";
    public static final String APP_ACTION_VIEW = "view";
    public static final String APP_ACTION_LIST = "index";
    public static final String APP_ACTION_DELETE = "delete";
    public static final String APP_ACTION_PRINT = "print";
    public static final String APP_ACTION_PRINT_GLOBAL = "print_global";
    public static final String APP_ACTION_AJAX = "ajax";
    public static final String APP_ACTION_ERROR = "error";

    // #region Ajax action
    public static final String ACTION_TRANSMIT = "transmit";
    public static final String ACTION_ORDONNANCER = "ordonnancer";
    public static final String ACTION_VALIDATE = "validate";
    public static final String ACTION_RETURN = "return";
    public static final String ACTION_DIFFER = "differ";
    public static final String ACTION_REJECT = "reject";
    public static final String ACTION_NOTIFICATION = "notification";
    public static final String ACTION_RECEPTION = "reception";
    public static final String ACTION_PRIS_EN_CHARGE = "priseencharge";
    public static final String ACTION_PAYEMENT = "payement";
    public static final String ACTION_CANCELLATION = "cancellation";
    public static final String ACTION_GET_MESSAGES = "get_messages";
    public static final String ACTION_EN_ATTENTE = "EN_ATTENTE";
    // #endregion

    // Ajax response
    public static final String AJAX_RESPONSE_RESULT = "result";
    public static final String AJAX_RESPONSE_MESSAGE = "message";
    public static final String AJAX_RESPONSE_TARGET_PAGE = "target_page";

    // MENU Action
    public static final String MENU_ACTION_CAN_ADD = "CAN_ADD";
    public static final String MENU_ACTION_CAN_EDIT = "CAN_EDIT";
    public static final String MENU_ACTION_CAN_DELETE = "CAN_DELETE";
    public static final String MENU_ACTION_CAN_VIEW = "CAN_VIEW";
    public static final String MENU_ACTION_CAN_PRINT = "CAN_PRINT";
    public static final String MENU_ACTION_CAN_TRANSMIT = "CAN_TRANSMIT";
    public static final String MENU_ACTION_CAN_TRANSMIT_OR_VALIDATE = "CAN_TRANSMIT_OR_VALIDATE";
    public static final String MENU_ACTION_CAN_TRANSMIT_AND_VALIDATE = "CAN_TRANSMIT_AND_VALIDATE";
    public static final String MENU_ACTION_CAN_VALIDATE = "CAN_VALIDATE";
    public static final String MENU_ACTION_CAN_REJECT = "CAN_REJECT";
    public static final String MENU_ACTION_CAN_RETURN = "CAN_RETURN";
    public static final String MENU_ACTION_CAN_DIFFER = "CAN_DIFFER";
    public static final String MENU_ACTION_CAN_PRINT_GLOBAL = "CAN_PRINT_GLOBAL";

    // #Code des statuts pour l'ouverture de l'exercice
    public static final String STATUT_NOUVEAU_CODE = "NEW";
    public static final String STATUT_OUVERTURE_CODE = "OPEN";
    public static final String STATUT_CLOTURE_CODE = "CLO";

    // Statut générique
    public static final String STATUT_TRANSMIS_CODE = "TRS";
    public static final String STATUT_VALIDE_CODE = "VAL";
    public static final String STATUT_REJETE_CODE = "REJ";

    // report output
    public static final String PDF = "pdf";
    public static final String WORD = "docx";
    public static final String EXCEL = "excel";
    public static final String CSV = "csv";
    public static final String TEXT = "text";
    public static final String ETAT_PARAMS = "ETAT_PARAMS";

    // ##
    public static final String GROUPE_ORGANISME = "ORG";
    public static final String GROUPE_STRUCTURE = "STR";

    // ##
    public static final String STATUT_INIT = "NEW";
    public static final String STATUT_TRANSMIS = "TRS";
    public static final String STATUT_VALIDE = "VAL";

    // caching manager

    // statut cache
    public static final String ROLE_CACHE = "roleCache";
    public static final String TYPE_ROLE_CACHE = "typeRoleCache";
    public static final String BATIMENT_CACHE = "batimentCache";
    public static final String LOCALITE_CACHE = "localiteCache";
    public static final String PAYS_CACHE = "paysCache";
    public static final String PROFESSION_CACHE = "professionCache";
    public static final String PERIODE_CACHE = "periodeCache";
    public static final String MINISTERE_CACHE = "ministereCache";
    public static final String MATERIEL_CACHE = "materielCache";
    public static final String VILLE_CACHE = "villeCache";
    // Type de localite
    public static final String TYPE_LOCALITE_DISTRICT_CODE = "DIS";
    public static final String TYPE_LOCALITE_REGION_CODE = "REG";
    public static final String TYPE_LOCALITE_DEPARTEMENT_CODE = "DEPT";
    public static final String TYPE_LOCALITE_LOCALITE_CODE = "LOC";
    public static final String DISTRICT_ABJ_CODE = null;
    public static final String DISTRICT_YKRO_CODE = null;
    public static final String PAYS_CI = null;
    public static final int NBRE_TABLE_STAT = 5;
    public static final int OBJECTIF_ATTEINT = 2;
    public static final int OBJECTIF_NON_ATTEINT = 1;
    public static final int TYPE_OBS_ACHEVE_ID = 6;
    public static final int TYPE_OBS_NON_ACHEVE_ID = 1;
    
    public static final List<String> MOIS = List.of(
    	    "Janvier","Février","Mars","Avril","Mai","Juin",
    	    "Juillet","Août","Septembre","Octobre","Novembre","Décembre"
    	);

}

