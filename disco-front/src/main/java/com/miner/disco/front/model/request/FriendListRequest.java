package com.miner.disco.front.model.request;

import com.miner.disco.basic.model.request.Pagination;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Created by lubycoder@163.com 2019/1/3
 */
@Getter
@Setter
public class FriendListRequest extends Pagination {

    private static final long serialVersionUID = 8524405364574019620L;

    private Long userId;

}
