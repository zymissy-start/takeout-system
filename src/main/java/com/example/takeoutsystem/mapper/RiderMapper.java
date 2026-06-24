package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.RiderInfo;
import com.example.takeoutsystem.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 骑手基础数据访问层。
 * 主要负责骑手登录、骑手信息查询、骑手状态更新。
 */
public interface RiderMapper {

    @Select("""
            SELECT
                user_id AS userId,
                username,
                password,
                real_name AS realName,
                phone,
                role_type AS roleType,
                credit_score AS creditScore,
                status,
                create_time AS createTime
            FROM sys_user
            WHERE username = #{username}
              AND password = #{password}
              AND role_type = 3
              AND status = 1
            """)
    SysUser findRiderByUsernameAndPassword(@Param("username") String username,
                                           @Param("password") String password);

    @Select("""
            SELECT
                r.rider_id AS riderId,
                r.user_id AS userId,
                u.username AS username,
                u.real_name AS realName,
                u.phone AS phone,
                r.is_full_time AS isFullTime,
                r.status AS status,
<<<<<<< HEAD
                r.avg_speed AS avgSpeed
=======
                r.avg_speed AS avgSpeed,
                IFNULL(r.rider_level, 0) AS riderLevel,
                IFNULL(r.rider_title, '普通骑手') AS riderTitle,
                IFNULL(r.total_finished_count, 0) AS totalFinishedCount
>>>>>>> origin/feature-user-rider-merchant
            FROM rider_info r
            JOIN sys_user u ON r.user_id = u.user_id
            WHERE r.user_id = #{userId}
            """)
    RiderInfo findRiderInfoByUserId(@Param("userId") Integer userId);

    @Update("""
            UPDATE rider_info
            SET status = #{status}
            WHERE user_id = #{userId}
            """)
    int updateRiderStatus(@Param("userId") Integer userId,
                          @Param("status") Integer status);
<<<<<<< HEAD
}
=======


    @Select("""
            SELECT COUNT(*)
            FROM delivery_order
            WHERE rider_id = #{riderUserId}
              AND status = 4
            """)
    Integer countFinishedOrders(@Param("riderUserId") Integer riderUserId);

    @Update("""
            UPDATE rider_info
            SET total_finished_count = #{totalFinishedCount},
                rider_level = #{riderLevel},
                rider_title = #{riderTitle}
            WHERE user_id = #{riderUserId}
            """)
    int updateRiderLevel(@Param("riderUserId") Integer riderUserId,
                         @Param("totalFinishedCount") Integer totalFinishedCount,
                         @Param("riderLevel") Integer riderLevel,
                         @Param("riderTitle") String riderTitle);
}
>>>>>>> origin/feature-user-rider-merchant
