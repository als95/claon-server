package coLaon.ClaonBack.center.service;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.center.domain.HoldInfo;
import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.center.domain.SectorInfo;
import coLaon.ClaonBack.center.dto.CenterCreateRequestDto;
import coLaon.ClaonBack.center.dto.CenterResponseDto;
import coLaon.ClaonBack.center.dto.HoldInfoResponseDto;
import coLaon.ClaonBack.center.repository.CenterRepository;
import coLaon.ClaonBack.center.repository.HoldInfoRepository;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.validator.IsAdminValidator;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CenterService {
    private final UserRepository userRepository;
    private final CenterRepository centerRepository;
    private final HoldInfoRepository holdInfoRepository;

    @Transactional
    public CenterResponseDto create(
            String userId,
            CenterCreateRequestDto requestDto
    ) {
        User admin = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        IsAdminValidator validator = IsAdminValidator.of(admin.getEmail());
        validator.validate();

        Center center = this.centerRepository.save(
                Center.of(
                        requestDto.getName(),
                        requestDto.getAddress(),
                        requestDto.getTel(),
                        requestDto.getWebUrl(),
                        requestDto.getInstagramUrl(),
                        requestDto.getYoutubeUrl(),
                        requestDto.getImgList()
                                .stream().map(dto -> CenterImg.of(dto.getUrl()))
                                .collect(Collectors.toList()),
                        requestDto.getOperatingTimeList()
                                .stream().map(dto -> OperatingTime.of(dto.getDay(), dto.getStart(), dto.getEnd()))
                                .collect(Collectors.toList()),
                        requestDto.getFacilities(),
                        requestDto.getChargeList()
                                .stream().map(dto -> Charge.of(dto.getName(), dto.getFee()))
                                .collect(Collectors.toList()),
                        requestDto.getChargeImg(),
                        requestDto.getHoldInfoImg(),
                        requestDto.getSectorInfoList()
                                .stream().map(dto -> SectorInfo.of(dto.getName(), dto.getStart(), dto.getEnd()))
                                .collect(Collectors.toList())
                )
        );

        return CenterResponseDto.from(
                center,
                requestDto.getHoldInfoList()
                        .stream()
                        .map(holdInfo -> this.holdInfoRepository.save(
                                HoldInfo.of(
                                        holdInfo.getName(),
                                        holdInfo.getImg(),
                                        center
                                )
                        ))
                        .collect(Collectors.toList())
        );
    }

    @Transactional(readOnly = true)
    public List<HoldInfoResponseDto> findHoldInfoByCenterId(String centerId) {
        Center center = centerRepository.findById(centerId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "암장 정보를 찾을 수 없습니다."
                )
        );

        return holdInfoRepository.findAllByCenter(center)
                .stream()
                .map(HoldInfoResponseDto::from)
                .collect(Collectors.toList());
    }
}