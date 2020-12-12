package apptribus.com.tribus.util;

/**
 * Created by User on 5/14/2017.
 */

public class Constantes {

    //PATH DATABASE USUÁRIOS
    public static final String INDIVIDUAL_USERS = "Users-name";
    public static final String GENERAL_USERS = "Users";
    public static final String IMAGES_USERS = "Users-images";
    public static final String VOICES_USERS = "Users-voices";
    public static final String VIDEOS_USERS = "Users-videos";
    public static final String TALKERS_AUDIO_MESSAGES = "Talkers-Audio-Messages";
    public static final String TALKERS_VIDEOS_MESSAGES = "Talkers-Videos-Messages";
    public static final String TALKERS_IMAGES_MESSAGES = "Talkers-Images-Messages";
    public static final String USERS_MESSAGES = "User-Messages";
    public static final String TALKERS_MESSAGES = "Talkers-Messages";
    public static final String CHAT_TALKER = "Chat-Talker";
    public static final String CHAT_CURRENT_USER = "Chat-Current-User";
    public static final String USERS_TALKS = "User-Talks";
    public static final String USERS_TALKS_ADDED = "User-Talks-Added";
    public static final String USERS_TALKS_ACCEPTED = "User-Talks-Accepted";
    public static final String BLOCKED_TALKERS = "Blocked-Talkers";
    public static final String FOLLOWERS = "Followers";
    public static final String ADMINS = "Admins";
    public static final String ADMINS_PERMISSIONS = "Admins-Permissions";
    public static final String ADMINS_INVITATIONS = "Admins-Invitations";
    //public static final String USERS_TALKERS_PERMISSIONS = "Users-Talkers-Permissions";
    public static final String USERS_TALKERS_PERMISSIONS = "Users-Talkers-Permissions";
    public static final String USERS_TALKERS_INVITATIONS = "Users-Talkers-Invitations";
    public static final String USERS_REMOVED_TRIBUS = "Users-Remove-Tribus";
    public static final String USERS_ENDED_TRIBUS = "Users-Ended-Tribus";

    //PATH DATABASE TRIBUS
    public static final String INDIVIDUAL_TRIBUS = "Tribus-name";
    public static final String TRIBUS_UNIQUE = "Tribus-Unique-Name";
    public static final String GENERAL_TRIBUS = "Tribus";
    public static final String IMAGES_TRIBUS = "Tribus-images";
    public static final String TRIBUS_AUDIO_MESSAGES = "Tribus-Audio-Messages";
    public static final String TRIBUS_VIDEOS_MESSAGES = "Tribus-Videos-Messages";
    public static final String TRIBUS_IMAGES_MESSAGES = "Tribus-Images-Messages";
    //public static final String VOICES_TRIBUS = "Tribus-voices";
    //public static final String VIDEOS_TRIBUS = "Tribus-videos";
    public static final String TRIBUS_MESSAGES = "Tribus-Messages";
    public static final String TRIBUS_TOPICS = "Tribus-Topics";
    public static final String TRIBUS_THEMATICS = "Tribus-Thematics";
    public static final String TRIBUS = "Tribus";
    public static final String TRIBUS_FOLLOWERS = "Tribus-Followers";
    public static final String TRIBUS_RECOMMENDATIONS = "Tribus-Recommendations";
    public static final String TRIBUS_COMMENTS = "Tribus-Comments";
    public static final String LIKES_TRIBUS_COMMENTS = "Likes-Tribus-Comments";
    public static final String DISLIKES_TRIBUS_COMMENTS = "Dislikes-Tribus-Comments";
    public static final String TRIBUS_SURVEY = "Tribus-Survey";


    //MESSAGE TOAST
    public static final String NOME_USUARIO_INDISPONIVEL = "Nome de usuário indisponível! Por favor, escolha outro.";
    public static final String CAMPO_NOME_USUARIO_VAZIO = "Informe um nome de usuário";
    public static final String NOME_TRIBU_INDISPONIVEL = "Este nome está indisponível para a sua Tribu! Por favor, escolha outro.";
    public static final String EMAIL_EXISTE = "Este e-mail já existe em nosso banco de dados.";
    public static final String TRIBU_CRIADA_SUCESSO = "Tribu criada com sucesso!";
    public static final String USUARIO_CADASTRADO_SUCESSO = "Usuário cadastrado com sucesso!";
    public static final String ERRO_CRIAR_TRIBU = "Ocorreu um erro ao criar sua Tribu";


    //MESSAGE PROGRESS DIALOG
    public static final String CONSULTANDO_USER_NAME = "Consultando o nome de usuário...";
    public static final String CADASTRANDO_USUARIO = "Cadastrando usuário...";
    public static final String CRIANDO_TRIBU = "Criando a Tribu...";


    //CONSTANTS OF THE APPLICATION CONTEXT
    public static final String STATE_KEY = "state_key";
    public static final String USER_ID = "user_id";
    public static final String CONTACT_ID = "contact";
    public static final String FOLLOWER_ID = "follower";
    public static final String TRIBU_UNIQUE_NAME = "mTribuUniqueName";
    public static final String TRIBU_KEY = "mTribuKey";
    public static final String TOPIC_KEY = "topicKey";
    public static final String TOPIC_NAME = "topicName";
    public static final String TRIBU_NAME = "tribuName";
    public static final String MESSAGE_REFERENCE = "message_reference";
    public static final String POTENTIAL_FOLLOWER_KEY = "potential_follower_key";
    public static final String NAME_USER = "name";
    public static final String USERNAME_USER = "username";
    public static final String UPDATE_LAST_MESSAGE = "updateLastMessage";
    public static final String LINK_INTO_MESSAGE = "link";
    public static final String FROM_NOTIFICATION = "fromNotification";
    public static final String SURVEY_QUESTION = "survey_question";
    public static final String SURVEY_VOTE = "Survey_Votes";
    public static final String FROM_CHAT_TRIBUS = "from chat tribus";

    //TOPICS NAME
    public static final String NEW_TRIBUS = "NewTribu";


    //MESAGES TYPES
    public static final String TEXT = "TEXT";
    public static final String MESSAGE_REPLY = "MESSAGE_REPLY";
    public static final String LINK = "LINK";
    public static final String IMAGE = "IMAGE";
    public static final String VIDEO = "VIDEO";
    public static final String VOICE = "VOICE";
    public static final String REMOVED = "REMOVED";

    //UPDATES TYPE
    public static final String TAG_UPDATE = "TAG_UPDATE";


    //VIEW TYPES
    public static final int TEXT_MESSAGE_USER_LEFT = 0;
    public static final int EMOTICON_MESSAGE_USER_LEFT = 1;
    public static final int VOICE_MESSAGE_USER_LEFT = 2;
    public static final int IMAGE_MESSAGE_USER_LEFT = 3;
    public static final int VIDEO_MESSAGE_USER_LEFT = 4;

    public static final int TEXT_MESSAGE_USER_RIGHT = 5;
    public static final int EMOTICON_MESSAGE_USER_RIGHT = 6;
    public static final int VOICE_MESSAGE_USER_RIGHT = 7;
    public static final int IMAGE_MESSAGE_USER_RIGHT = 8;
    public static final int VIDEO_MESSAGE_USER_RIGHT = 9;

    //removed messages
    public static final int REMOVED_MESSAGE_USER_LEFT = 10;
    public static final int REMOVED_MESSAGE_USER_RIGHT = 11;
    public static final int REPLY_MESSAGE_USER_LEFT = 12;

    //TITLE TRIBUS ADDED - BOTTOM NAVIGATION VIEW
    public static final String TRIBUS_ADICIONADAS = "Tribus adicionadas";
    public static final String TRIBUS_REMOVIDAS = "Tribus removidas";
    public static final String TRIBUS_ENCERRADAS= "Tribus encerradas";
    public static final String SOLICITACOES_ENVIADAS = "Solicitações enviadas";


    //TITLE TALKS ADDED - BOTTOM NAVIGATION VIEW
    public static final String CONTATOS_ADICIONADOS = "Contatos adicionados";
    public static final String CONVITES_RECEBIDOS = "Convites recebidos";
    public static final String REMOVER_CONTATOS = "Remover contatos";
    public static final String CONVITES_ENVIADOS = "Convites enviados";


    //CONSTANTS FRAGMENT CONTAINER
    public static final String TOPIC = "topic";
    public static final String SURVEY = "survey";
    public static final String EVENT = "event";
    public static final String CAMPAIGN = "campaign";
    public static final String SHARED_MEDIA = "shared_media";


    //NEW FIRESTORE COLLECTIONS AND FIELDS
    public static final String ALL = "Todas";
    public static final String TRIBUS_PARTICIPANTS = "Participants";
    public static final String PARTICIPATING = "Participating";
    public static final String REMOVED_TRIBUS = "Removed-Tribus";
    public static final String CLOSED_TRIBUS = "Closed-Tribus";
    public static final String TOPICS = "Topics";
    public static final String TOPIC_MESSAGES = "Messages";
    public static final String REPLY_MESSAGES = "Reply-Messages";
    public static final String SURVEYS = "Surveys";
    public static final String SURVEY_VOTES = "Votes";
    public static final String USER_PERMISSIONS = "User-Permissions"; //user accept another user as a contact
    public static final String CONTACTS = "Contacts";
    public static final String CONTACTS_ACCEPTED = "Contacts-Accepted";
    public static final String CONTACTS_ADDED = "Contacts-Added";
    public static final String USER_INVITATIONS = "User-Invitations";//user invite another user as a contact
    public static final String TRIBUS_INVITATIONS = "Tribus-Invitations"; //user request participating of one tribu
    public static final String ADMIN_PERMISSION = "Permissions"; //admin accept another user as a follower
    public static final String ADMIN = "Admin";
    public static final String KEY_TRIBU = "keyTribu";
    public static final String KEY = "key";
    public static final String PROFILE_IMAGE = "profile_image";
    public static final String IMAGE_URL = "imageUrl";
    public static final String PROFILE = "profile";
    public static final String THUMB = "thumb";
    public static final String PROFILE_THUMB = "profile.thumb";
    public static final String PROFILE_IMAGE_URL = "profile.imageUrl";
    public static final String CONTACTS_MESSAGES = "Contacts-Messages";
    public static final String MESSAGE_TAGS = "Tags";
    public static final String TRIBU = "Tribu";
    public static final String GENERAL_TAGS = "Tribus-Tags";
    public static final String TAG_COLLECTION = "Tag";
    public static final String USERS_UPDATES = "Updates";



    //NEW FIRESTORE FIELDS
    public static final String TIMESTAMP = "timestampCreated.timestamp";
    public static final String PROFILE_CREATION_DATE = "profile.creationDate";
    public static final String DATE = "date";
    public static final String DATE_INVITATION = "dateInvitation";
    public static final String EDITED = "edited";
    public static final String TOT_VOTES = "totVotes";
    public static final String NUM_VOTES_FIRST_ANSWER = "numVotesFirstAnswer";
    public static final String NUM_VOTES_SECOND_ANSWER = "numVotesSecondAnswer";
    public static final String NUM_VOTES_THIRD_ANSWER = "numVotesThirdAnswer";
    public static final String NUM_VOTES_FOURTH_ANSWER = "numVotesFourthAnswer";
    public static final String NUM_VOTES_FIFTH_ANSWER = "numVotesFifthAnswer";
    public static final String PROFILE_THEMATIC = "profile.thematic";
    public static final String THEMATIC = "thematic";

}
