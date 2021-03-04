package io.github.singlerr;

import io.github.singlerr.db.Database;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class Main extends Plugin implements Listener {
    private Configuration config;
    private static Main instance;
    private String URL,id,pass,table,msg,msg_dberror;
    private Database db;
    public void onEnable() {

        if(! getDataFolder().exists())
            getDataFolder().mkdir();

        File conf = new File(getDataFolder(),"config.yml");

        if(! conf.exists()){
            try(InputStream in = getResourceAsStream("config.yml")){
                Files.copy(in,conf.toPath());
            }catch (IOException ex){
                ex.printStackTrace();
                getLogger().log(Level.SEVERE,"콘피그 파일 생성 중 오류");
            }
        }
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(conf);

        }catch (IOException ex){
            ex.printStackTrace();
            getLogger().log(Level.SEVERE,"콘피그 파일 로드 중 오류");
        }

        URL = config.getString("url");
        id = config.getString("id");
        pass = config.getString("pass");
        table = config.getString("table");
        msg = config.getString("msg");
        msg_dberror = config.getString("msg_db_error");
        try{
            db = new Database(URL,id,pass);
        }catch (SQLException ex1){
            ex1.printStackTrace();
            getLogger().log(Level.SEVERE,"URL, ID, PASS 중 올바르지 않은 것이 있습니다. 콘피그를 확인하세요.");
        }catch (ClassNotFoundException ex2){
            getLogger().log(Level.SEVERE,"MariaDB가 설치되어 있는 것이 맞습니까?");
        }
        

        getProxy().getPluginManager().registerListener(this,this);
        getProxy().getPluginManager().registerCommand(this,new ReloadCMD("abreload"));
        getLogger().log(Level.INFO,"데이터베이스 연결 성공, 플러그인 활성화 완료");
    }

    public static Main getInstance() {
        return instance;
    }

    public void reload(CommandSender commandSender){
        if(! getDataFolder().exists())
            getDataFolder().mkdir();

        File conf = new File(getDataFolder(),"config.yml");

        if(! conf.exists()){
            try(InputStream in = getResourceAsStream("config.yml")){
                Files.copy(in,conf.toPath());
            }catch (IOException ex){
                ex.printStackTrace();

                commandSender.sendMessage(new ComponentBuilder("콘피그 파일 생성 중 오류").color(ChatColor.RED).create());
                getLogger().log(Level.SEVERE,"콘피그 파일 생성 중 오류");
            }
        }
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(conf);

        }catch (IOException ex){
            ex.printStackTrace();
            commandSender.sendMessage(new ComponentBuilder("콘피그 파일 로드 중 오류").color(ChatColor.RED).create());
        }

        URL = config.getString("url");
        id = config.getString("id");
        pass = config.getString("pass");
        table = config.getString("table");
        msg = config.getString("msg");
        msg_dberror = config.getString("msg_db_error");
        try{
            db = new Database(URL,id,pass);
        }catch (SQLException ex1){
            commandSender.sendMessage(new ComponentBuilder("URL, ID, PASS 중 올바르지 않은 것이 있습니다. 콘피그를 확인하세요.").color(ChatColor.RED).create());
        }catch (ClassNotFoundException ex2){
            commandSender.sendMessage(new ComponentBuilder("MariaDB가 설치되어 있는 것이 맞습니까?").color(ChatColor.RED).create());
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
        public void onLogin(LoginEvent e){
            if(! e.isCancelled()){
                try {
                    String uuid = e.getConnection().getUniqueId().toString();
                    Statement st = db.getInstance().createStatement();
                    ResultSet result = st.executeQuery("select * from "+table+" where uuid = '"+uuid+"'");
                    if(! result.isBeforeFirst()){
                        e.setCancelReason(ChatColor.stripColor(msg));
                        e.setCancelled(true);
                    }else{
                        getLogger().log(Level.INFO,"(PASS) UUID: "+uuid);
                    }
                }catch (SQLException ex){
                    ex.printStackTrace();
                    e.setCancelReason(ChatColor.stripColor(msg_dberror));
                    e.setCancelled(true);
                }
                }
        }
}
