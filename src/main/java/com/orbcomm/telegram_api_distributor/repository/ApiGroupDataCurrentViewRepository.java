package com.orbcomm.telegram_api_distributor.repository;

import com.orbcomm.telegram_api_distributor.entity.ApiGroupDataCurrentView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApiGroupDataCurrentViewRepository extends JpaRepository<ApiGroupDataCurrentView, Void>, JpaSpecificationExecutor<ApiGroupDataCurrentView> {

    @Query(value = "select   " +
            "    di.device_id   " +
            "    ,di.vhcle_nm   " +
            "    ,di.manage_crp_id   " +
            "     ,gateway.return_crpnm(di.manage_crp_id) as manage_crp_nm   " +
            "    ,di.crp_id   " +
            "    ,gateway.return_crpnm(di.crp_id) as crp_nm   " +
            "    ,dlm.update_date as event_date   " +
            "    ,gateway.decode_location(dlm.latitude) as latitude   " +
            "    ,gateway.decode_location(dlm.longitude) as longitude   " +
            "       from (select user_id,group_id,use_yn,user_expired_date,user_nm from gateway.user_info ui   " +
            "where user_id = :p_user_id  " +
            ") ui2   " +
            "join gateway.device_group_mp dgm   " +
            "on dgm.group_id = ui2.group_id and dgm.device_id = :p_device_id and dgm.use_at ='Y'   " +
            "join gateway.device_info di   " +
            "on dgm.device_id = di.device_id   " +
            "join gateway.device_location_mp dlm   " +
            "on dlm.device_id = di.device_id",nativeQuery = true)
    ApiGroupDataCurrentView getLocationData(@Param("p_user_id") String p_user_id, @Param("p_device_id") String p_device_id);


    @Query(value = "select user_id from gateway.user_info ui   " +
            " where user_id = (   " +
            "     select user_id from gateway.kakao_bot_user_info   " +
            "     where conversation_id = :p_chat_id)   " +
            "   and use_yn = 'Y'     " +
            "   and user_expired_date>now() ",nativeQuery = true)
    String getUserID(@Param("p_chat_id") String p_chat_id);

}