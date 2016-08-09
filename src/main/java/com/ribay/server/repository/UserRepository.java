package com.ribay.server.repository;

import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.query.indexes.StringBinIndex;
import com.ribay.server.db.MyRiakClient;
import com.ribay.server.material.User;
import com.ribay.server.util.RibayProperties;
import com.ribay.server.util.riak.RiakObjectBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Chris on 09.08.2016.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UserRepository {

    private enum UserSettingChangeEnum {
        NAME, PASSWORD
    }

    @Autowired
    private RibayProperties properties;

    @Autowired
    private MyRiakClient client;

    public User editUserName(String uuid, String newName) throws Exception {
        return editUserSetting(UserSettingChangeEnum.NAME, uuid, newName);
    }

    public User editPassword(String uuid, String newPassword) throws Exception {
        return editUserSetting(UserSettingChangeEnum.PASSWORD, uuid, newPassword);
    }

    private User editUserSetting(UserSettingChangeEnum changeProperty,String uuid, String newValue) throws Exception {
        String bucket = properties.getBucketUsers();
        User user;

        Location location = new Location(new Namespace(bucket), uuid);

        FetchValue fetchOp = new FetchValue.Builder(location).build();
        user = client.execute(fetchOp).getValue(User.class);

        if(user != null) {
            changeUserProperty(user, changeProperty, newValue);

            RiakObject riakObj = new RiakObjectBuilder(user)
                    .withIndex(StringBinIndex.named("index_email"), user.getEmailAddress())
                    .build();

            StoreValue storeOp = new StoreValue.Builder(riakObj).withLocation(location).build();
            client.execute(storeOp);
            return user;
        } else {
            return null;
        }
    }

    private User changeUserProperty(User user, UserSettingChangeEnum changeProperty, String newValue) {
        User changeUser = user;

        switch(changeProperty) {
            case NAME:
                user.setName(newValue);
                break;
            case PASSWORD:
                user.setPassword(newValue);
                break;
        }

        return changeUser;
    }
}
