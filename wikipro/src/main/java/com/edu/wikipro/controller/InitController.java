package com.edu.wikipro.controller;

import com.edu.wikipro.common.Result;
import com.edu.wikipro.entity.Banner;
import com.edu.wikipro.entity.Doc;
import com.edu.wikipro.service.BannerService;
import com.edu.wikipro.service.DocService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/init")
@CrossOrigin(origins = "*", maxAge = 3600)
public class InitController {

    @GetMapping("/test")
    public Result<?> test() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("message", "Hello World");
        map.put("time", System.currentTimeMillis());
        return Result.success("测试成功", map);
    }

    @Resource
    private BannerService bannerService;

    @Resource
    private DocService docService;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/createTable")
    public Result<?> createTable() {
        createCollectionTable();
        createCartTable();
        createOrderItemTable();
        createOrderTable();
        createDocTable();
        createBannerTable();
        return Result.success("表结构创建成功");
    }

    @GetMapping("/data")
    public Result<?> initData() {
        initBanners();
        initDocs();
        return Result.success("数据初始化成功");
    }

    @GetMapping("/all")
    public Result<?> initAll() {
        createCollectionTable();
        createCartTable();
        createOrderItemTable();
        createOrderTable();
        createDocTable();
        createBannerTable();
        initBanners();
        initDocs();
        return Result.success("表结构和数据初始化成功");
    }

    private void createDocTable() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0");
        jdbcTemplate.execute("DROP TABLE IF EXISTS doc");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1");
        String sql = "CREATE TABLE doc (" +
                "doc_id INT AUTO_INCREMENT PRIMARY KEY," +
                "doc_title VARCHAR(255) NOT NULL," +
                "doc_desc TEXT," +
                "image VARCHAR(500)," +
                "author VARCHAR(100)," +
                "views INT DEFAULT 0," +
                "category VARCHAR(100)," +
                "stock INT DEFAULT 100," +
                "sales INT DEFAULT 0," +
                "price DOUBLE DEFAULT 0" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        jdbcTemplate.execute(sql);
    }

    private void createBannerTable() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0");
        jdbcTemplate.execute("DROP TABLE IF EXISTS wx_banner");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1");
        String sql = "CREATE TABLE wx_banner (" +
                "banner_id INT AUTO_INCREMENT PRIMARY KEY," +
                "imgurl VARCHAR(500) NOT NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        jdbcTemplate.execute(sql);
    }

    private void createCollectionTable() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0");
        jdbcTemplate.execute("DROP TABLE IF EXISTS collection");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1");
        String sql = "CREATE TABLE collection (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "doc_id INT NOT NULL," +
                "FOREIGN KEY (doc_id) REFERENCES doc(doc_id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        jdbcTemplate.execute(sql);
    }

    private void initBanners() {
        bannerService.remove(null);
        
        Banner banner1 = new Banner();
        banner1.setImgurl("https://picsum.photos/800/400?random=100");

        Banner banner2 = new Banner();
        banner2.setImgurl("https://picsum.photos/800/400?random=101");

        Banner banner3 = new Banner();
        banner3.setImgurl("https://picsum.photos/800/400?random=102");

        Banner banner4 = new Banner();
        banner4.setImgurl("https://picsum.photos/800/400?random=103");

        bannerService.saveBatch(java.util.Arrays.asList(banner1, banner2, banner3, banner4));
    }

    private void initDocs() {
        docService.remove(null);
        List<Doc> docs = new ArrayList<>();

        Doc doc1 = new Doc();
        doc1.setDocTitle("蓝鲸：海洋巨无霸");
        doc1.setDocDesc("了解世界上最大的动物，蓝鲸的生态习性和保护现状。蓝鲸体长可达30米，体重可达180吨，是地球上最大的生物。");
        doc1.setImage("https://picsum.photos/400/300?random=200");
        doc1.setAuthor("海洋研究所");
        doc1.setViews(5621);
        doc1.setCategory("哺乳动物");
        doc1.setStock(100);
        doc1.setSales(56);
        doc1.setPrice(29.9);
        docs.add(doc1);

        Doc doc2 = new Doc();
        doc2.setDocTitle("珊瑚礁生态系统");
        doc2.setDocDesc("探索珊瑚礁的多样性，了解它们如何支撑海洋生命。珊瑚礁被称为海洋热带雨林，拥有极高的生物多样性。");
        doc2.setImage("https://picsum.photos/400/300?random=201");
        doc2.setAuthor("生态保护协会");
        doc2.setViews(3421);
        doc2.setCategory("生态系统");
        doc2.setStock(80);
        doc2.setSales(34);
        doc2.setPrice(19.9);
        docs.add(doc2);

        Doc doc3 = new Doc();
        doc3.setDocTitle("海豚的智慧");
        doc3.setDocDesc("揭秘海豚的智商水平和社会行为，令人惊叹的海洋精灵。海豚拥有复杂的语言系统和高超的学习能力。");
        doc3.setImage("https://picsum.photos/400/300?random=202");
        doc3.setAuthor("海洋生物学家");
        doc3.setViews(4123);
        doc3.setCategory("哺乳动物");
        doc3.setStock(120);
        doc3.setSales(41);
        doc3.setPrice(25.9);
        docs.add(doc3);

        Doc doc4 = new Doc();
        doc4.setDocTitle("深海生物探秘");
        doc4.setDocDesc("深入海底世界，发现那些神秘的深海生物。深海环境极端，但孕育了许多奇特的生物。");
        doc4.setImage("https://picsum.photos/400/300?random=203");
        doc4.setAuthor("深海探险家");
        doc4.setViews(2876);
        doc4.setCategory("深海生物");
        doc4.setStock(60);
        doc4.setSales(28);
        doc4.setPrice(35.9);
        docs.add(doc4);

        Doc doc5 = new Doc();
        doc5.setDocTitle("海龟的迁徙之旅");
        doc5.setDocDesc("追踪海龟漫长的迁徙路线，了解它们的生存挑战。海龟每年要游数千公里返回出生地。");
        doc5.setImage("https://picsum.photos/400/300?random=204");
        doc5.setAuthor("野生动物保护");
        doc5.setViews(3567);
        doc5.setCategory("爬行动物");
        doc5.setStock(90);
        doc5.setSales(35);
        doc5.setPrice(22.9);
        docs.add(doc5);

        Doc doc6 = new Doc();
        doc6.setDocTitle("鲨鱼：海洋霸主");
        doc6.setDocDesc("揭开鲨鱼的神秘面纱，了解它们的种类和习性。鲨鱼是顶级捕食者，对维持海洋生态平衡至关重要。");
        doc6.setImage("https://picsum.photos/400/300?random=205");
        doc6.setAuthor("鲨鱼研究中心");
        doc6.setViews(4892);
        doc6.setCategory("鱼类");
        doc6.setStock(100);
        doc6.setSales(48);
        doc6.setPrice(28.9);
        docs.add(doc6);

        Doc doc7 = new Doc();
        doc7.setDocTitle("海洋生物多样性保护");
        doc7.setDocDesc("探讨海洋生物多样性的重要性，以及如何保护我们的蓝色星球。每一个物种都在生态系统中扮演重要角色。");
        doc7.setImage("https://picsum.photos/700/300?random=206");
        doc7.setAuthor("联合国环境署");
        doc7.setViews(6789);
        doc7.setCategory("生态保护");
        doc7.setStock(150);
        doc7.setSales(67);
        doc7.setPrice(39.9);
        docs.add(doc7);

        Doc doc8 = new Doc();
        doc8.setDocTitle("气候变化对海洋的影响");
        doc8.setDocDesc("分析全球变暖如何影响海洋生态系统，带来的挑战和应对措施。海洋温度上升正在改变整个生态系统。");
        doc8.setImage("https://picsum.photos/700/300?random=207");
        doc8.setAuthor("气候研究中心");
        doc8.setViews(4567);
        doc8.setCategory("气候变化");
        doc8.setStock(70);
        doc8.setSales(45);
        doc8.setPrice(32.9);
        docs.add(doc8);

        Doc doc9 = new Doc();
        doc9.setDocTitle("海洋食物链详解");
        doc9.setDocDesc("从浮游生物到顶级捕食者，完整解析海洋生态系统的能量流动。食物链是生态系统的核心。");
        doc9.setImage("https://picsum.photos/700/300?random=208");
        doc9.setAuthor("生态学家");
        doc9.setViews(3210);
        doc9.setCategory("生态系统");
        doc9.setStock(85);
        doc9.setSales(32);
        doc9.setPrice(24.9);
        docs.add(doc9);

        Doc doc10 = new Doc();
        doc10.setDocTitle("章鱼的伪装术");
        doc10.setDocDesc("揭秘章鱼惊人的变色能力和伪装技巧。章鱼可以在瞬间改变体色和纹理，融入周围环境。");
        doc10.setImage("https://picsum.photos/400/300?random=209");
        doc10.setAuthor("海洋科普");
        doc10.setViews(892);
        doc10.setCategory("软体动物");
        doc10.setStock(50);
        doc10.setSales(8);
        doc10.setPrice(18.9);
        docs.add(doc10);

        Doc doc11 = new Doc();
        doc11.setDocTitle("企鹅的生存挑战");
        doc11.setDocDesc("气候变化下企鹅栖息地的变化和生存危机。南极企鹅正面临着栖息地减少的威胁。");
        doc11.setImage("https://picsum.photos/400/300?random=210");
        doc11.setAuthor("南极考察队");
        doc11.setViews(1234);
        doc11.setCategory("鸟类");
        doc11.setStock(45);
        doc11.setSales(12);
        doc11.setPrice(26.9);
        docs.add(doc11);

        Doc doc12 = new Doc();
        doc12.setDocTitle("珊瑚白化现象");
        doc12.setDocDesc("解释珊瑚白化的原因和对海洋生态的影响。海水温度上升导致珊瑚失去共生藻，变成白色。");
        doc12.setImage("https://picsum.photos/400/300?random=211");
        doc12.setAuthor("珊瑚研究");
        doc12.setViews(756);
        doc12.setCategory("生态系统");
        doc12.setStock(35);
        doc12.setSales(7);
        doc12.setPrice(21.9);
        docs.add(doc12);

        Doc doc13 = new Doc();
        doc13.setDocTitle("海狮的生活习性");
        doc13.setDocDesc("了解海狮的社会结构和行为特点。海狮是非常社会化的动物，喜欢成群结队生活。");
        doc13.setImage("https://picsum.photos/400/300?random=212");
        doc13.setAuthor("海洋哺乳动物研究");
        doc13.setViews(2345);
        doc13.setCategory("哺乳动物");
        doc13.setStock(65);
        doc13.setSales(23);
        doc13.setPrice(24.9);
        docs.add(doc13);

        Doc doc14 = new Doc();
        doc14.setDocTitle("海马的独特繁殖方式");
        doc14.setDocDesc("探索海马与众不同的繁殖机制。海马是唯一由雄性负责孵化后代的动物。");
        doc14.setImage("https://picsum.photos/400/300?random=213");
        doc14.setAuthor("海洋生物学家");
        doc14.setViews(1876);
        doc14.setCategory("鱼类");
        doc14.setStock(55);
        doc14.setSales(18);
        doc14.setPrice(19.9);
        docs.add(doc14);

        Doc doc15 = new Doc();
        doc15.setDocTitle("水母的神秘世界");
        doc15.setDocDesc("揭秘水母的生命历程和独特的生物发光能力。水母已经在地球上生存了数亿年。");
        doc15.setImage("https://picsum.photos/400/300?random=214");
        doc15.setAuthor("深海生物研究所");
        doc15.setViews(3456);
        doc15.setCategory("软体动物");
        doc15.setStock(70);
        doc15.setSales(34);
        doc15.setPrice(27.9);
        docs.add(doc15);

        Doc doc16 = new Doc();
        doc16.setDocTitle("海藻的生态作用");
        doc16.setDocDesc("了解海藻在海洋生态系统中的重要角色。海藻是海洋食物链的基础，提供氧气和栖息地。");
        doc16.setImage("https://picsum.photos/400/300?random=215");
        doc16.setAuthor("海洋植物研究");
        doc16.setViews(2134);
        doc16.setCategory("植物");
        doc16.setStock(85);
        doc16.setSales(21);
        doc16.setPrice(16.9);
        docs.add(doc16);

        Doc doc17 = new Doc();
        doc17.setDocTitle("海象的冰上生活");
        doc17.setDocDesc("探索北极海象的生存环境和生活习性。海象依赖海冰进行休息和繁殖。");
        doc17.setImage("https://picsum.photos/400/300?random=216");
        doc17.setAuthor("北极研究中心");
        doc17.setViews(1567);
        doc17.setCategory("哺乳动物");
        doc17.setStock(40);
        doc17.setSales(15);
        doc17.setPrice(28.9);
        docs.add(doc17);

        Doc doc18 = new Doc();
        doc18.setDocTitle("海星的再生能力");
        doc18.setDocDesc("揭秘海星令人惊叹的身体再生能力。海星可以从一小部分身体重新长出完整的个体。");
        doc18.setImage("https://picsum.photos/400/300?random=217");
        doc18.setAuthor("海洋生物学家");
        doc18.setViews(2890);
        doc18.setCategory("棘皮动物");
        doc18.setStock(58);
        doc18.setSales(28);
        doc18.setPrice(22.9);
        docs.add(doc18);

        Doc doc19 = new Doc();
        doc19.setDocTitle("电鳗的发电原理");
        doc19.setDocDesc("了解电鳗如何产生强大的电击。电鳗是自然界中最强大的'发电机'。");
        doc19.setImage("https://picsum.photos/400/300?random=218");
        doc19.setAuthor("生物物理学研究");
        doc19.setViews(3123);
        doc19.setCategory("鱼类");
        doc19.setStock(62);
        doc19.setSales(31);
        doc19.setPrice(25.9);
        docs.add(doc19);

        Doc doc20 = new Doc();
        doc20.setDocTitle("海葵与小丑鱼的共生关系");
        doc20.setDocDesc("探索海葵和小丑鱼之间奇妙的共生关系。小丑鱼为海葵清理寄生虫，海葵为小丑鱼提供保护。");
        doc20.setImage("https://picsum.photos/400/300?random=219");
        doc20.setAuthor("海洋生态研究所");
        doc20.setViews(4567);
        doc20.setCategory("生态系统");
        doc20.setStock(95);
        doc20.setSales(45);
        doc20.setPrice(23.9);
        docs.add(doc20);

        Doc doc21 = new Doc();
        doc21.setDocTitle("乌贼的喷墨逃生");
        doc21.setDocDesc("揭秘乌贼如何利用墨汁逃脱天敌的追捕。乌贼的墨汁不仅能迷惑敌人，还能干扰嗅觉。");
        doc21.setImage("https://picsum.photos/400/300?random=220");
        doc21.setAuthor("软体动物研究");
        doc21.setViews(2345);
        doc21.setCategory("软体动物");
        doc21.setStock(75);
        doc21.setSales(23);
        doc21.setPrice(21.9);
        docs.add(doc21);

        Doc doc22 = new Doc();
        doc22.setDocTitle("海豹的游泳技巧");
        doc22.setDocDesc("了解海豹如何在水中快速游动和深潜。海豹是出色的游泳健将，可以潜入数百米深的海底。");
        doc22.setImage("https://picsum.photos/400/300?random=221");
        doc22.setAuthor("海洋哺乳动物研究");
        doc22.setViews(1876);
        doc22.setCategory("哺乳动物");
        doc22.setStock(68);
        doc22.setSales(18);
        doc22.setPrice(25.9);
        docs.add(doc22);

        Doc doc23 = new Doc();
        doc23.setDocTitle("海牛的温和天性");
        doc23.setDocDesc("探索海牛的生活习性和保护现状。海牛是海洋中最温顺的动物之一，也是濒危物种。");
        doc23.setImage("https://picsum.photos/400/300?random=222");
        doc23.setAuthor("野生动物保护协会");
        doc23.setViews(2134);
        doc23.setCategory("哺乳动物");
        doc23.setStock(45);
        doc23.setSales(21);
        doc23.setPrice(27.9);
        docs.add(doc23);

        Doc doc24 = new Doc();
        doc24.setDocTitle("海龙的伪装艺术");
        doc24.setDocDesc("揭秘海龙惊人的伪装能力。海龙的身体形态与海草极为相似，很难被发现。");
        doc24.setImage("https://picsum.photos/400/300?random=223");
        doc24.setAuthor("鱼类研究所");
        doc24.setViews(1567);
        doc24.setCategory("鱼类");
        doc24.setStock(52);
        doc24.setSales(15);
        doc24.setPrice(19.9);
        docs.add(doc24);

        Doc doc25 = new Doc();
        doc25.setDocTitle("海胆的防御机制");
        doc25.setDocDesc("了解海胆如何利用棘刺保护自己。海胆的棘刺不仅能防御天敌，还能帮助移动。");
        doc25.setImage("https://picsum.photos/400/300?random=224");
        doc25.setAuthor("海洋生物学家");
        doc25.setViews(1890);
        doc25.setCategory("棘皮动物");
        doc25.setStock(58);
        doc25.setSales(18);
        doc25.setPrice(18.9);
        docs.add(doc25);

        Doc doc26 = new Doc();
        doc26.setDocTitle("贝类的珍珠形成");
        doc26.setDocDesc("探索珍珠的形成过程。当异物进入贝类体内时，贝类会分泌珍珠质将其包裹。");
        doc26.setImage("https://picsum.photos/400/300?random=225");
        doc26.setAuthor("贝类研究中心");
        doc26.setViews(3456);
        doc26.setCategory("软体动物");
        doc26.setStock(85);
        doc26.setSales(34);
        doc26.setPrice(24.9);
        docs.add(doc26);

        Doc doc27 = new Doc();
        doc27.setDocTitle("虾蟹的蜕壳生长");
        doc27.setDocDesc("了解虾蟹如何通过蜕壳实现生长。每次蜕壳后，虾蟹都会长大一些。");
        doc27.setImage("https://picsum.photos/400/300?random=226");
        doc27.setAuthor("甲壳动物研究");
        doc27.setViews(2345);
        doc27.setCategory("甲壳动物");
        doc27.setStock(92);
        doc27.setSales(23);
        doc27.setPrice(17.9);
        docs.add(doc27);

        Doc doc28 = new Doc();
        doc28.setDocTitle("海蛇的毒性之谜");
        doc28.setDocDesc("揭秘海蛇的毒性有多强。海蛇的毒液是所有蛇类中最致命的之一。");
        doc28.setImage("https://picsum.photos/400/300?random=227");
        doc28.setAuthor("爬行动物研究所");
        doc28.setViews(2890);
        doc28.setCategory("爬行动物");
        doc28.setStock(42);
        doc28.setSales(28);
        doc28.setPrice(26.9);
        docs.add(doc28);

        Doc doc29 = new Doc();
        doc29.setDocTitle("浮游生物的重要性");
        doc29.setDocDesc("探讨浮游生物在海洋生态中的基础作用。浮游生物是海洋食物链的基石。");
        doc29.setImage("https://picsum.photos/400/300?random=228");
        doc29.setAuthor("海洋生态学家");
        doc29.setViews(3123);
        doc29.setCategory("浮游生物");
        doc29.setStock(80);
        doc29.setSales(31);
        doc29.setPrice(23.9);
        docs.add(doc29);

        Doc doc30 = new Doc();
        doc30.setDocTitle("海绵的滤食生活");
        doc30.setDocDesc("了解海绵如何通过过滤海水获取食物。海绵是最简单的多细胞动物之一。");
        doc30.setImage("https://picsum.photos/400/300?random=229");
        doc30.setAuthor("海绵研究");
        doc30.setViews(1567);
        doc30.setCategory("海绵动物");
        doc30.setStock(45);
        doc30.setSales(15);
        doc30.setPrice(18.9);
        docs.add(doc30);

        Doc doc31 = new Doc();
        doc31.setDocTitle("海百合的远古生存");
        doc31.setDocDesc("探索海百合的进化历程。海百合是一种古老的海洋生物，已经存在了数亿年。");
        doc31.setImage("https://picsum.photos/400/300?random=230");
        doc31.setAuthor("古生物研究所");
        doc31.setViews(1876);
        doc31.setCategory("棘皮动物");
        doc31.setStock(52);
        doc31.setSales(18);
        doc31.setPrice(24.9);
        docs.add(doc31);

        Doc doc32 = new Doc();
        doc32.setDocTitle("鲸鲨的温和一面");
        doc32.setDocDesc("了解世界上最大的鱼类——鲸鲨。尽管体型巨大，但鲸鲨是温和的滤食者。");
        doc32.setImage("https://picsum.photos/400/300?random=231");
        doc32.setAuthor("鲨鱼研究中心");
        doc32.setViews(4567);
        doc32.setCategory("鱼类");
        doc32.setStock(75);
        doc32.setSales(45);
        doc32.setPrice(32.9);
        docs.add(doc32);

        Doc doc33 = new Doc();
        doc33.setDocTitle("蝠鲼的优雅飞行");
        doc33.setDocDesc("探索蝠鲼如何在水中优雅地'飞行'。蝠鲼的游泳姿态如同水中的舞者。");
        doc33.setImage("https://picsum.photos/400/300?random=232");
        doc33.setAuthor("鱼类研究所");
        doc33.setViews(3456);
        doc33.setCategory("鱼类");
        doc33.setStock(68);
        doc33.setSales(34);
        doc33.setPrice(29.9);
        docs.add(doc33);

        Doc doc34 = new Doc();
        doc34.setDocTitle("海獭的工具使用");
        doc34.setDocDesc("揭秘海獭如何使用工具打开贝壳。海獭是少数会使用工具的动物之一。");
        doc34.setImage("https://picsum.photos/400/300?random=233");
        doc34.setAuthor("海洋哺乳动物研究");
        doc34.setViews(2890);
        doc34.setCategory("哺乳动物");
        doc34.setStock(58);
        doc34.setSales(28);
        doc34.setPrice(27.9);
        docs.add(doc34);

        Doc doc35 = new Doc();
        doc35.setDocTitle("三文鱼的洄游奇迹");
        doc35.setDocDesc("了解三文鱼如何从海洋返回出生地繁殖。这是一段充满挑战的旅程。");
        doc35.setImage("https://picsum.photos/400/300?random=234");
        doc35.setAuthor("鱼类研究所");
        doc35.setViews(3123);
        doc35.setCategory("鱼类");
        doc35.setStock(72);
        doc35.setSales(31);
        doc35.setPrice(25.9);
        docs.add(doc35);

        Doc doc36 = new Doc();
        doc36.setDocTitle("深海热泉生态系统");
        doc36.setDocDesc("探索深海热泉周围独特的生态系统。这里的生物完全不依赖阳光生存。");
        doc36.setImage("https://picsum.photos/400/300?random=235");
        doc36.setAuthor("深海探险家");
        doc36.setViews(2345);
        doc36.setCategory("深海生物");
        doc36.setStock(60);
        doc36.setSales(23);
        doc36.setPrice(36.9);
        docs.add(doc36);

        docService.saveBatch(docs);
    }

    private void createCartTable() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0");
        jdbcTemplate.execute("DROP TABLE IF EXISTS cart");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1");
        String sql = "CREATE TABLE cart (" +
                "cart_id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "goods_id INT NOT NULL," +
                "quantity INT DEFAULT 1," +
                "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        jdbcTemplate.execute(sql);
    }

    private void createOrderTable() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0");
        jdbcTemplate.execute("DROP TABLE IF EXISTS `order`");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1");
        String sql = "CREATE TABLE `order` (" +
                "order_id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "total_price DOUBLE DEFAULT 0," +
                "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "payment_status VARCHAR(20) DEFAULT 'fail'" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        jdbcTemplate.execute(sql);
    }

    private void createOrderItemTable() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0");
        jdbcTemplate.execute("DROP TABLE IF EXISTS order_item");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1");
        String sql = "CREATE TABLE order_item (" +
                "item_id INT AUTO_INCREMENT PRIMARY KEY," +
                "order_id INT NOT NULL," +
                "goods_id INT NOT NULL," +
                "quantity INT DEFAULT 1," +
                "price DOUBLE DEFAULT 0" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        jdbcTemplate.execute(sql);
    }
}