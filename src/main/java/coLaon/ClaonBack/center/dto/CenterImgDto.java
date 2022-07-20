package coLaon.ClaonBack.center.dto;

import coLaon.ClaonBack.center.domain.CenterImg;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CenterImgDto {
    private String url;

    public static CenterImgDto from(CenterImg centerImg) {
        return new CenterImgDto(
                centerImg.getUrl()
        );
    }
}
