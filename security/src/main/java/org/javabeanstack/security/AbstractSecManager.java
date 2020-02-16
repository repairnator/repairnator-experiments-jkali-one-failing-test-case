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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.apache.log4j.Logger;

import org.javabeanstack.data.IDataRow;
import org.javabeanstack.error.ErrorManager;
import org.javabeanstack.util.Strings;
import org.javabeanstack.data.IGenericDAO;

/**
 * Es un wraper de la clase Sessions. Se encarga de logeo de los usuario, 
 * creación de las sesiones, manejo de la expiración de cada sesión.
 * 
 * El test unitario se encuentra en TestProjects clase
 *     py.com.oym.test.data.TestSesiones
 * 
 * @author Jorge Enciso
 */
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public abstract class AbstractSecManager  implements ISecManager, Serializable{
    private static final Logger LOGGER = Logger.getLogger(AbstractSecManager.class);    

    protected abstract IGenericDAO getDAO();
    protected abstract ISessions getSessions();
    /**
     *  Crea una sesión de usuario para acceso a la app
     * 
     * @param userLogin     usuario
     * @param password      password
     * @param idcompany     empresa que esta solicitando ingresar
     * @param idleSessionExpireInMinutes    minutos sin actividad antes de cerrar la sesión.
     * @return objeto conteniendo datos del login exitoso o rechazado
     */
    @Override
    public IUserSession createSession(String userLogin, String password, Object idcompany, Integer idleSessionExpireInMinutes) {
        return getSessions().createSession(userLogin,  password, idcompany, idleSessionExpireInMinutes);
    }
    
    
    /**
     * Vuelve a crear la sesión con el acceso a una nueva empresa
     * 
     * @param sessionId identificador de la sesión.
     * @param idcompany identificador de la empresa a la que se solicita el nuevo acceso.
     * @return objeto sesión
     */
    @Override
    public IUserSession reCreateSession(String sessionId, Object idcompany) {
        return getSessions().reCreateSession(sessionId,  idcompany);
    }

    /**
     * Devuelve verdadero si sus credenciales para el logeo son válidas o falso si no
     * 
     * @param userLogin
     * @param password
     * @return verdadero si sus credenciales para el logeo son válidas o falso si no
     * @throws Exception 
     */
    @Override
    public Boolean login(String userLogin, String password) throws Exception {
        IUserSession sesion = getSessions().login(userLogin, password);
        return sesion != null && sesion.getUser() != null;
    }
    
    /**
     * Devuelve el objeto userSession posterior a la authenticación del usuario.
     * @param userLogin usuario
     * @param password  palabra clave.
     * @return  userSession conteniendo información de la sesión del usuario
     * @throws Exception 
     */
    @Override
    public IUserSession login2(String userLogin, String password) throws Exception {
        return getSessions().login(userLogin, password);
    }    
    

    /**
     * Devuelve verdadero o falso dependiendo si el identificador de la sesión
     * es válida o no.
     * 
     * @param sesionId  identificador de la sesión a consultar su válidez
     * @return verdadero o falso dependiendo si el identificador de la sesión.
     */
    @Override
    public Boolean isSesionIdValid(String sesionId){
        IUserSession sesion;
        sesion = getSessions().getUserSession(sesionId);
        if (sesion == null){
            return false;
        }
        if (sesion.getUser() == null){
            return false;
        }
        return sesion.getSessionId() != null;
    }
    
    
    /**
     * Devuelve los roles asignados a un usuario solicitado
     * 
     * @param userLogin    codigo del usuaio
     * @return los roles asignados a un usuario solicitado
     */
    @Override
    public String getUserRol(String userLogin){
        return "";
    }
    
    
    @Override
    public String getCompanyList(){
        return "";
    }    
    
    /**
     * Devuelve si un usuario es miembro de un grupo.
     * @param user  usuario
     * @param userGroup grupo
     * @return verdadero o falso si pertenece a un grupo dado.
     */
    @Override
    public Boolean isUserMemberOf(String user, String userGroup){
        LOGGER.debug("isUserMemberOf");
        String sqlComando;
        sqlComando = "select a "
                + " from AppUserMember a"
                + " where a.usergroup.code  = '{userGroup}' "
                + " and   a.usermember.code = '{user}'";
        
        Map<String, String> params = new HashMap<>();
        params.put("user", user.trim());
        params.put("userGroup", userGroup.trim());        
        sqlComando = Strings.textMerge(sqlComando, params);
        try {
            List<IDataRow> result = getDAO().findListByQuery("", sqlComando, null);
            return !result.isEmpty();
        }
        catch (Exception exp){
            ErrorManager.showError(exp, LOGGER);
        }
        return false;
    }

    /**
     * Cierra una sesión.
     * @param sessionId identificador de la sesión.
     */
    @Override
    public void logout(String sessionId) {
        getSessions().logout(sessionId);
    }
    
    /**
     * Cierra la sesión
     * @param userSession objeto conteniendo la información de la sesión a cerrar.
     */
    @Override
    public void logout(IUserSession userSession) {
        if (userSession != null && userSession.getSessionId() != null){
            getSessions().logout(userSession.getSessionId());            
        }
    }
}
