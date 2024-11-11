package com.tmesh.im.app.chat.controller;

import com.tmesh.im.app.chat.service.ChatApplyService;
import com.tmesh.im.app.chat.service.ChatFriendService;
import com.tmesh.im.app.chat.vo.ApplyVo01;
import com.tmesh.im.app.chat.vo.FriendVo02;
import com.tmesh.im.common.version.ApiVersion;
import com.tmesh.im.common.version.VersionEnum;
import com.tmesh.im.common.web.controller.BaseController;
import com.tmesh.im.common.web.domain.AjaxResult;
import com.tmesh.im.common.web.page.TableDataInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 申请控制器
 */
@RestController
@Slf4j
@RequestMapping("/apply")
public class ApplyController extends BaseController {

    @Resource
    private ChatFriendService chatFriendService;

    @Resource
    private ChatApplyService chatApplyService;

    /**
     * 申请添加
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Validated FriendVo02 friendVo) {
        this.chatFriendService.applyFriend(friendVo);
        return AjaxResult.success();
    }

    /**
     * 申请记录
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/list")
    public TableDataInfo list() {
        super.startPage("create_time desc");
        return getDataTable(this.chatApplyService.list());
    }

    /**
     * 申请详情
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/info/{applyId}")
    public AjaxResult getInfo(@PathVariable Long applyId) {
        return AjaxResult.success(this.chatApplyService.getInfo(applyId));
    }

    /**
     * 同意申请
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/agree")
    public AjaxResult agree(@RequestBody @Validated ApplyVo01 applyVo) {
        this.chatFriendService.agree(applyVo.getApplyId());
        return AjaxResult.success();
    }

    /**
     * 拒绝申请
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/refused")
    public AjaxResult refused(@RequestBody @Validated ApplyVo01 applyVo) {
        this.chatFriendService.refused(applyVo.getApplyId());
        return AjaxResult.success();
    }

    /**
     * 忽略申请
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/ignore")
    public AjaxResult ignore(@RequestBody @Validated ApplyVo01 applyVo) {
        this.chatFriendService.ignore(applyVo.getApplyId());
        return AjaxResult.success();
    }
}
