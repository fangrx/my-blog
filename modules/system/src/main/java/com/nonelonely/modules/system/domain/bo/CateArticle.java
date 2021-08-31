package com.nonelonely.modules.system.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CateArticle {
    long id;
    String title;
    String xtitle;
}
