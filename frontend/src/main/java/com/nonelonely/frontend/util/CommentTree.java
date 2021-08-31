package com.nonelonely.frontend.util;

import com.nonelonely.common.utils.ToolUtil;
import com.nonelonely.modules.system.domain.Comment;
import com.nonelonely.modules.system.domain.User;
import com.nonelonely.modules.system.repository.CommentRepository;
import com.nonelonely.modules.system.repository.UserRepository;
import lombok.Data;
import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;
import nl.bitwalker.useragentutils.UserAgent;
import nl.bitwalker.useragentutils.Version;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ProjectName: noteblogv4
 * @Package: me.wuwenbin.noteblogv4.model.pojo.business
 * @ClassName: ${CLASS_NAME}
 * @Author: nonelonely
 * @CreateDate: 2019-04-20 13:12
 * @Version: 1.0
 * @Copyright: Copyright Reserved (c) 2019, http://www.nonelonely.com
 * @Dependency:
 * @Description: java类作用描述
 * -
 * ****************************************************************
 * @UpdateUser: 13434
 * @UpdateDate: 2019-04-20 13:12
 * @UpdateRemark: The modified content
 * ****************************************************************
 * -
 */
@Data
public class CommentTree {



    private Long id;

    private Long parentId;

    private  Long levelId;

    private Long userId;


    private Long useredId;



    private String content;


    private String img;

    private Date time;

    private  String address;
    private String beReplyName;

    private  String replyName;
    private  String osname;
    private  String browse;

    private List<CommentTree> replyBody;

    public CommentTree(Long id, Long parentId,Long levelId, String content, String img, Date time, String address, String beReplyName, String replyName, String osname, String browse) {
        this.id = id;
        this.parentId = parentId;
        this.content = content;
        this.img = img;
        this.time = time;
        this.address = address;
        this.beReplyName = beReplyName;
        this.replyName = replyName;
        this.osname = osname;
        this.browse = browse;
        this.levelId =levelId;
    }

    private static CommentTree findChildren(CommentTree dataNode, List<CommentTree> dataNodes) {
        for (CommentTree it : dataNodes) {
            if (dataNode.getId().equals(it.getLevelId())) {
                if (dataNode.getReplyBody() == null) {
                    dataNode.setReplyBody(new ArrayList<>());
                }
                dataNode.getReplyBody().add(findChildren(it, dataNodes));
            }
        }
        return dataNode;
    }
    /**
     * 使用递归方法建树
     *
     * @param dataNodes
     * @return
     */
    public static List<CommentTree> buildByRecursive(List<Comment> dataNodes, CommentRepository commentRepository) {
        List<CommentTree> trees = new ArrayList<>(dataNodes.size());
        dataNodes.forEach(dataNode -> {
           User nbSysUser= dataNode.getCreateBy();
           String userAgent=dataNode.getUserAgent();
           String beReplyName="";
          if (dataNode.getParentId() != 0 ) {
              beReplyName = commentRepository.findById(dataNode.getParentId()).get().getCreateBy().getNickname();
          }
            CommentTree commentTree = new CommentTree(dataNode.getId(),
                     dataNode.getParentId(),dataNode.getLevelId(),
                    dataNode.getComment(),nbSysUser.getPicture(),dataNode.getCreateDate(),dataNode.getIpCnAddr(),beReplyName, nbSysUser.getNickname(), ToolUtil.detectOS(dataNode.getUserAgent()),ToolUtil.browser(dataNode.getUserAgent()).getName()+"("+ToolUtil.browser(dataNode.getUserAgent()).getVersion(dataNode.getUserAgent())+")");
            trees.add(commentTree);
        });
        List<CommentTree> data = new ArrayList<>(20);
        for (CommentTree dataNode : trees) {
            if (0 == dataNode.getLevelId()) {
                data.add(findChildren(dataNode, trees));
            }
        }
        return data;
    }



}
