package com.tmesh.im.app.collect.controlloer;



import com.tmesh.im.app.collect.domain.ChatCollect;
import com.tmesh.im.app.collect.service.ChatCollectService;
import com.tmesh.im.app.collect.vo.CollectVo01;
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
 * @description : 收藏控制器
 */
@RestController
@Slf4j
@RequestMapping("/collect")
public class CollectController extends BaseController {

    @Resource
    private ChatCollectService collectService;

    /**
     * 增加
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/add")
    public AjaxResult addCollect(@Validated @RequestBody CollectVo01 collectVo) {
        this.collectService.addCollect(collectVo);
        return AjaxResult.success();
    }

    /**
     * 删除
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/remove/{collectId}")
    public AjaxResult remove(@PathVariable Long collectId) {
        this.collectService.deleteCollect(collectId);
        return AjaxResult.success();
    }

    /**
     * 列表
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/list")
    public TableDataInfo list(ChatCollect collect) {
        startPage("create_time desc");
        return getDataTable(this.collectService.collectList(collect));
    }

}
