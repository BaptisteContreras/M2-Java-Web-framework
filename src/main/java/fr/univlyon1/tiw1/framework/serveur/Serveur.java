package fr.univlyon1.tiw1.framework.serveur;

import fr.univlyon1.tiw1.framework.exception.ReferenceException;
import fr.univlyon1.tiw1.framework.loader.ApplicationConfig;
import fr.univlyon1.tiw1.framework.vue.ResponseEntity;
import org.picocontainer.Parameter;
import java.util.Map;

public interface Serveur {

    /**
     * FORMAT DE REQUÊTE :
     * maClasseCible@maFonctionCible
     *
     * ce qui donne par exemple:
     * RdpController@ouvrirSession
     */
    ResponseEntity<?> processRequest(String commande, Map<String, String> parameters);

    void register(ApplicationConfig applicationConfig) throws ClassNotFoundException;

    void registerRegistry();

    void bind(String path, Object what, Parameter...parameters) throws ReferenceException;

    Object lookup(String name) throws ReferenceException;

    void dispose();
}
