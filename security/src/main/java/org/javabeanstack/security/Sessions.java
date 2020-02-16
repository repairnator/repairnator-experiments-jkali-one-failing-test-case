/*
* JavaBeanStack FrameWork
*
* Copyright (C) 2017 Jorge Enciso
* Email: jorge.enciso.r@gmail.com
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 3 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
* MA 02110-1301  USA
 */
package org.javabeanstack.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Startup;
import org.apache.log4j.Logger;

import org.javabeanstack.crypto.CipherUtil;
import org.javabeanstack.crypto.DigestUtil;
import org.javabeanstack.error.ErrorManager;
import org.javabeanstack.error.ErrorReg;
import org.javabeanstack.data.IGenericDAO;
import org.javabeanstack.util.Dates;
import org.javabeanstack.model.IAppCompany;
import org.javabeanstack.model.IAppCompanyAllowed;
import org.javabeanstack.model.IAppUser;
import org.javabeanstack.util.Fn;

/**
 * Es la clase encargada de todas las sesiones de usuarios. Guarda información
 * de cada sesión y gestión la creación y expiración de la sesión.
 *
 * El test unitario se encuentra en TestProjects clase
 * py.com.oym.test.data.TestSesiones
 *
 * @author Jorge Enciso
 */
//@Singleton
@Startup
@Lock(LockType.READ)
public class Sessions implements ISessions{

    private static final Logger LOGGER = Logger.getLogger(Sessions.class);
    protected final Map<String, Object> sessionVar = new HashMap<>();
    protected boolean oneSessionPerUser = false;
    private SecretKey secretKey;

    @EJB
    protected IGenericDAO dao;

    /**
     * Se ejecuta al instanciarse esta clase.
     *
     */
    @PostConstruct
    private void init() {
        try {
            secretKey = CipherUtil.getSecureRandomKey(CipherUtil.BLOWFISH);
        } catch (NoSuchAlgorithmException ex) {
            ErrorManager.showError(ex, LOGGER);
        }
    }

    /**
     * Crea una sesión de usuario para acceso a la app
     *
     * @param userLogin usuario
     * @param password password
     * @param idcompany empresa que esta solicitando ingresar
     * @param idleSessionExpireInMinutes minutos sin actividad antes de cerrar
     * la sesión.
     * @return objeto conteniendo datos del login exitoso o rechazado
     */
    @Override
    @Lock(LockType.WRITE)
    public IUserSession createSession(String userLogin, String password, Object idcompany, Integer idleSessionExpireInMinutes) {
        LOGGER.debug("CREATESESSION IN");
        try {
            // Verifcar si coincide usuario, contraseña y pasar resultado para ser procesado
            IUserSession session = login(userLogin, password);
            processCreateSession(session, idcompany, idleSessionExpireInMinutes);
            return session;
        } catch (Exception exp) {
            ErrorManager.showError(exp, LOGGER);
        }
        return null;
    }

    /**
     * Procesa la creación de la sesión del usuario al sistema.
     *
     * @param session variable creada en el metodo login que va a ser procesada.
     * @param idcompany empresa que esta solicitando ingresar
     * @param idleSessionExpireInMinutes minutos sin actividad antes de cerrar
     * la sesión.
     * @return verdadero o falso si pudo o no crear la sesión.
     * @throws Exception
     */
    @Lock(LockType.WRITE)
    protected boolean processCreateSession(IUserSession session, Object idcompany, Integer idleSessionExpireInMinutes) throws Exception {
        LOGGER.debug("PROCESSCREATESESSION IN");
        if (session == null || session.getUser() == null) {
            return false;
        }
        String sessionId = createSessionId(session);
        // Si se permite solo una sesión por usuario 
        if (oneSessionPerUser) {
            // Válidar que el usuario no este ya logueado                    
            IUserSession sessionCtrl = getUserSession(encrypt(sessionId));
            if (sessionCtrl != null && sessionCtrl.getUser() != null) {
                session.setUser(null);
                String mensaje = "Este usuario tiene una sesión activa";
                LOGGER.debug(mensaje);
                session.setError(new ErrorReg(mensaje, 4, ""));
                return false;
            }
        }
        // Verificar si tiene permiso para acceder a los datos de la empresa
        if (!checkCompanyAccess(((IAppUser) session.getUser()).getIduser(), (Long) idcompany)) {
            session.setUser(null);
            String mensaje = "No tiene autorización para acceder a esta empresa";
            LOGGER.debug(mensaje);
            session.setError(new ErrorReg(mensaje, 4, ""));
            return false;
        }

        Map<String, Object> parameters = new HashMap();
        parameters.put("idcompany", idcompany);

        IAppCompany company = dao.findByQuery(null,
                "select o from AppCompanyLight o "
                + " where idcompany = :idcompany", parameters);

        // Agregar atributos adicionales a la sesión
        String persistUnit = company.getPersistentUnit().trim();
        session.setPersistenceUnit(persistUnit); //Unidad de persistencia
        session.setCompany(company); // Empresa logueada
        session.setIdCompany(Long.parseLong(idcompany.toString()));

        // Id de sesión encriptada por seguridad.
        session.setSessionId(encrypt(sessionId));
        // Tiempo de expiración en minutos desde ultima actividad
        session.setIdleSessionExpireInMinutes(idleSessionExpireInMinutes);

        // Metodo que se ejecuta al final del proceso con el fin de que en clases derivadas
        // se pueda realizar tareas adicionales como anexar otros atributos a la sesión.
        afterCreateSession(session);
        // Agregar sesión al pool de sesiones
        sessionVar.put(sessionId, session);
        LOGGER.debug("Sesión creada: "+sessionId);                
        return true;
    }

    protected void afterCreateSession(IUserSession session) throws Exception {
    }

    /**
     * Vuelve a crear la sesión con el acceso a una nueva empresa
     *
     * @param sessionIdEncrypted identificador de la sesión.
     * @param idcompany identificador de la empresa a la que se solicita el
     * nuevo acceso.
     * @return objeto sesión
     */
    @Override
    @Lock(LockType.WRITE)
    public IUserSession reCreateSession(String sessionIdEncrypted, Object idcompany) {
        LOGGER.debug("RECREATESESSION IN");
        String sessionId = decrypt(sessionIdEncrypted);
        // Verificar válides de la sesión
        UserSession session = getUserSession(sessionIdEncrypted);
        if (session == null || session.getError() != null) {
            if (session == null) {
                return null;
            }
            LOGGER.debug(session.getError().getMessage());
            return session;
        }
        // Eliminar sesión 
        sessionVar.remove(sessionId);
        try {
            //Crear nueva sesión
            processCreateSession(session, idcompany, null);
            return session;
        } catch (Exception exp) {
            ErrorManager.showError(exp, LOGGER);
        }
        return null;
    }

    /**
     * Devuelve verdadero si sus credenciales para el logeo son válidas o falso
     * si no
     *
     * @param userLogin usuario
     * @param password contraseña.
     * @return userSession conteniendo información de la sesión del usuario
     * @throws Exception
     */
    @Override
    public IUserSession login(String userLogin, String password) throws Exception {
        LOGGER.debug("LOGIN IN");
        String mensaje;
        Map<String, Object> params = new HashMap<>();
        params.put("userLogin", userLogin);
        UserSession userSession;
        if (userLogin != null) {
            // Verificar existencia del usuario
            IAppUser usuario = dao.findByQuery(null,
                    "select o from AppUserLight o where code = :userLogin",
                    params);

            userSession = new UserSession();
            // Verificar que exista el usuario
            if (usuario == null) {
                mensaje = "Este usuario " + userLogin + " no existe";
                LOGGER.debug(mensaje);
                userSession.setError(new ErrorReg(mensaje, 1, ""));
                return userSession;
            }
            // Verificar que el usuario este activo.
            if (usuario.getDisable()) {
                mensaje = "La cuenta " + usuario.getLogin().trim() + " esta inactivo";
                LOGGER.info(mensaje);
                userSession.setError(new ErrorReg(mensaje, 2, ""));
                return userSession;
            }
            // Verificar que no expiro la cuenta
            if (usuario.getExpiredDate().before(Dates.now())) {
                mensaje = "La cuenta " + usuario.getLogin() + " expiro";
                LOGGER.debug(mensaje);
                userSession.setError(new ErrorReg(mensaje, 2, ""));
                return userSession;
            }
            // Verificar que la contraseña sea correcta
            String claveEncriptada = getEncryptedPass(usuario, password);
            if (!claveEncriptada.equals(usuario.getPass())) {
                userSession.setError(new ErrorReg("Contraseña incorrecta", 3, ""));
                return userSession;
            }
            userSession.setUser(usuario);
            return userSession;
        }
        return null;
    }

    /**
     * Cierra una sesión
     *
     * @param sessionIdEncrypted identificador de la sesión a cerrar
     */
    @Override
    @Lock(LockType.WRITE)
    public void logout(String sessionIdEncrypted) {
        LOGGER.debug("LOGOUT IN");
        try {
            String sessionId = decrypt(sessionIdEncrypted);
            sessionVar.remove(sessionId);
        } catch (Exception ex) {
            //
        }
    }

    /**
     * Chequea si un usuario tiene permiso a acceder a una empresa determinada
     *
     * @param iduser identificador del usuario
     * @param idcompany identificador de la empresa
     * @return verdadero si tiene permiso el usuario y falso si no
     * @throws Exception
     */
    @Override
    public Boolean checkCompanyAccess(Long iduser, Long idcompany) throws Exception {
        LOGGER.debug("CHECKCOMPANYACCESS IN");
        Map<String, Object> params = new HashMap<>();
        params.put("iduser", iduser);
        params.put("idcompany", idcompany);
        IAppCompanyAllowed row = dao.findByQuery(null,
                "select o from AppCompanyAllowed o "
                + "where iduser = :iduser  and idcompany = :idcompany",
                params);
        if (row != null) {
            return !row.getDeny();
        }
        return true;
    }

    /**
     * Devuelve un objeto sesión correspondiente a una sesión solicitada
     *
     * @param sessionIdEncrypted identificador de la sesión encriptada.
     * @return objeto con los datos de la sesión solicitada
     */
    @Override
    public UserSession getUserSession(String sessionIdEncrypted) {
        LOGGER.debug("GETUSERSESSION IN ");
        if (sessionIdEncrypted == null) {
            return null;
        }
        String sessionId = decrypt(sessionIdEncrypted);
        LOGGER.debug("SESSION ENCRYPTADA: "+sessionIdEncrypted);                
        LOGGER.debug("SESSION : "+sessionId);                
        UserSession sesion = (UserSession) sessionVar.get(sessionId);
        if (sesion != null) {
            Integer expireInMinutes = sesion.getIdleSessionExpireInMinutes();
            if (expireInMinutes == null) {
                expireInMinutes = 30;
            }
            // Verificar si ya expiro su sesión
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(sesion.getLastReference());
            cal2.setTime(new Date());
            long time1 = cal1.getTimeInMillis();
            long time2 = cal2.getTimeInMillis();
            // Diferencias en minutos desde la ultima vez que se hizo referencia a esta sesión.        
            long idleInMinutes = (time2 - time1) / (60 * 1000);
            if (idleInMinutes >= expireInMinutes) {
                sessionVar.remove(sessionId);
                sesion.setUser(null);
                String mensaje = "La sesión expiro";
                sesion.setError(new ErrorReg(mensaje, 6, ""));
                return sesion;
            }
            sesion.setLastReference(new Date());
        }
        return sesion;
    }

    /**
     * Devuelve un texto que identificará a la sesión de forma unica.
     *
     * @param userSession sesión creada desde el metodo login()
     * @return identificador de sesión.
     */
    protected String createSessionId(IUserSession userSession) {
        if (userSession == null || userSession.getUser() == null) {
            return null;
        }
        // Esto puede ser modificado en clases derivadas.
        String sessionId = userSession.getUser().getId() + ":" + userSession.getUser().getPass().toUpperCase().trim();
        Date fecha = new Date();
        if (!oneSessionPerUser) {
            sessionId += ":" + fecha.getTime();
        }
        return sessionId;
    }

    /**
     * Password cifrado que se utilizará para comparar con el password
     * almacenado en la base de datos.
     *
     * @param user usuario
     * @param password password
     * @return Password cifrado que se utilizará para comparar con el dato
     * almacenado en la base de datos.
     */
    protected String getEncryptedPass(IAppUser user, String password) {
        // Esto puede ser modificado en clases derivadas
        String md5 = user.getLogin().toUpperCase().trim() + ":" + password.trim();
        return DigestUtil.md5(md5).toUpperCase();
    }

    /**
     * Encripta un mensaje (en este caso el sesionId)
     *
     * @param msg mensaje
     * @return mensaje cifrado
     */
    protected final String encrypt(String msg) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(CipherUtil.BLOWFISH);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(msg.getBytes());
            return Fn.bytesToHex(encrypted);
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
            ErrorManager.showError(ex, LOGGER);
        }
        return null;
    }

    /**
     * Desencripta un mensaje (en este caso el sessionId)
     *
     * @param msgEncrypted mensaje encriptado.
     * @return mensaje descifrado
     */
    protected final String decrypt(String msgEncrypted) {
        try {
            byte[] encrypted = Fn.hexToByte(msgEncrypted);
            Cipher cipher = Cipher.getInstance(CipherUtil.BLOWFISH);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(encrypted));
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
            ErrorManager.showError(ex, LOGGER);
        }
        return null;
    }
}
