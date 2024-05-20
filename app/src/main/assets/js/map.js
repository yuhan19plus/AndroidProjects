// 사용자의 위치 정보를 가져오기 위한 URL 파라미터를 읽음
const urlParams = new URLSearchParams(window.location.search);
const userLat = parseFloat(urlParams.get('lat')) || 37.566826; // 기본 값: 서울
const userLng = parseFloat(urlParams.get('lng')) || 126.9786567; // 기본 값: 서울

var markers = []; // 지도에 표시된 마커들을 저장할 배열

// 지도 생성 및 초기화
var mapContainer = document.getElementById('map'), // 지도를 표시할 div
    mapOption = {
        center: new kakao.maps.LatLng(userLat, userLng), // 사용자의 위치를 기반으로 한 초기 중심 위치
        level: 3 // 지도 확대 레벨
    };

var map = new kakao.maps.Map(mapContainer, mapOption); // 지도를 생성
var ps = new kakao.maps.services.Places(); // 장소 검색 객체를 생성
var infowindow = new kakao.maps.InfoWindow({zIndex: 1}); // 인포윈도우를 생성

// 장소 검색을 수행하는 함수
function searchPlaces() {
    var keyword = document.getElementById('keyword').value; // 검색어를 가져옴
    var daiso = "다이소"; // 기본 검색어

    if (!keyword.replace(/^\s+|\s+$/g, '') || keyword === daiso) { // 검색어가 없거나 "다이소"일 경우
        keyword = daiso; // 기본 검색어로 설정
        // 현재 위치 기반 검색
        var options = {
            location: new kakao.maps.LatLng(userLat, userLng),
            radius: 2000 // 검색 반경 (2km)
        };
        ps.keywordSearch(keyword, placesSearchCB, options);
    } else {
        // 키워드 기반 검색
        ps.keywordSearch(keyword, placesSearchCB);
    }
}

// 장소 검색의 결과를 처리하는 콜백 함수
function placesSearchCB(data, status, pagination) {
    if (status === kakao.maps.services.Status.OK) { // 검색이 성공한 경우
        displayPlaces(data); // 검색 결과를 표시
        displayPagination(pagination); // 페이지 네이션을 표시
    } else if (status === kakao.maps.services.Status.ZERO_RESULT) { // 검색 결과가 없는 경우
        alert('검색 결과가 존재하지 않습니다.');
    } else if (status === kakao.maps.services.Status.ERROR) { // 에러가 발생한 경우
        alert('검색 중 오류가 발생했습니다.');
    }
}

// 검색 결과를 화면에 표시하는 함수
function displayPlaces(places) {
    var listEl = document.getElementById('placesList'), // 장소 리스트를 표시할 요소
        menuEl = document.getElementById('menu_wrap'),
        fragment = document.createDocumentFragment(),
        bounds = new kakao.maps.LatLngBounds(),
        listStr = '';

    removeAllChildNods(listEl); // 기존 검색 결과를 삭제
    removeMarker(); // 기존 마커를 삭제

    for (var i = 0; i < places.length; i++) {
        var placePosition = new kakao.maps.LatLng(places[i].y, places[i].x),
            marker = addMarker(placePosition, i),
            itemEl = getListItem(i, places[i]);

        bounds.extend(placePosition); // 검색 결과의 위치를 경계에 추가

        (function(marker, title) {
            kakao.maps.event.addListener(marker, 'mouseover', function() {
                displayInfowindow(marker, title);
            });

            kakao.maps.event.addListener(marker, 'mouseout', function() {
                infowindow.close();
            });

            itemEl.onclick = function() {
                map.setCenter(placePosition);
                displayInfowindow(marker, title);
            };
        })(marker, places[i].place_name);

        fragment.appendChild(itemEl);
    }

    listEl.appendChild(fragment); // 검색 결과를 화면에 추가
    map.setBounds(bounds); // 지도의 경계를 검색 결과에 맞게 설정
}

// 검색 결과의 각 항목을 생성하는 함수
function getListItem(index, places) {
    var el = document.createElement('li'), // 목록 요소를 생성
        itemStr = '<div class="info">' +
            '   <h5>' + (index + 1) + '. ' + places.place_name + '</h5>';

    if (places.road_address_name) {
        itemStr += '    <span>' + places.road_address_name + '</span>' +
            '   <span class="jibun gray">' + places.address_name + '</span>';
    } else {
        itemStr += '    <span>' + places.address_name + '</span>';
    }

    itemStr += '  <span class="tel">' + places.phone + '</span>' +
        '</div>';

    el.innerHTML = itemStr; // 목록 요소의 HTML을 설정
    el.className = 'item';

    return el; // 생성된 목록 요소를 반환
}

// 마커를 추가하는 함수
function addMarker(position, idx) {
    var imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_number_blue.png', // 마커 이미지의 URL
        imageSize = new kakao.maps.Size(36, 37), // 마커 이미지의 크기
        imgOptions = {
            spriteSize: new kakao.maps.Size(36, 691),
            spriteOrigin: new kakao.maps.Point(0, (idx * 46) + 10),
            offset: new kakao.maps.Point(13, 37)
        },
        markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imgOptions), // 마커 이미지를 생성
        marker = new kakao.maps.Marker({
            position: position,
            image: markerImage
        });

    marker.setMap(map); // 마커를 지도에 추가
    markers.push(marker); // 마커를 배열에 저장

    return marker; // 생성된 마커를 반환
}

// 기존 마커를 삭제하는 함수
function removeMarker() {
    for (var i = 0; i < markers.length; i++) {
        markers[i].setMap(null);
    }
    markers = []; // 마커 배열을 초기화
}

// 페이지 네이션을 표시하는 함수
function displayPagination(pagination) {
    var paginationEl = document.getElementById('pagination'),
        fragment = document.createDocumentFragment(),
        i;

    while (paginationEl.hasChildNodes()) {
        paginationEl.removeChild(paginationEl.lastChild);
    }

    for (i = 1; i <= pagination.last; i++) {
        var el = document.createElement('a');
        el.href = "#";
        el.innerHTML = i;

        if (i === pagination.current) {
            el.className = 'on';
        } else {
            el.onclick = (function(i) {
                return function() {
                    pagination.gotoPage(i);
                }
            })(i);
        }

        fragment.appendChild(el);
    }
    paginationEl.appendChild(fragment); // 페이지 네이션 요소를 추가
}

// 인포윈도우를 표시하는 함수
function displayInfowindow(marker, title) {
    var content = '<div style="padding:5px;z-index:1;font-size:20px; width:100%;">' + title + '</div>';
    infowindow.setContent(content); // 인포윈도우의 내용을 설정
    infowindow.open(map, marker); // 인포윈도우를 표시
}

// 지정된 요소의 모든 자식 노드를 삭제하는 함수
function removeAllChildNods(el) {
    while (el.hasChildNodes()) {
        el.removeChild(el.lastChild);
    }
}
