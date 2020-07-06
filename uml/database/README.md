## Database design

### Base Interface

| TableName | Type | Primary | KeyT | Foreign | Note | Example |
|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
| id | Long | o | - | - | AutoGenerate | 1 |
| create_date | Long | - | - | - | System.currentTimeMillis() | 2020-01-01 00:00:00 |
| update_date | Long | - | - | - | System.currentTimeMillis() | 2020-01-01 00:00:00 |

### UserInfo DB

| TableName | Type | Primary | KeyT | Foreign | Note | Example |
|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
| id | Long | o | - | o | SelectedAlbum#user_id | 1 |
| create_date | Long | - | - | - | - | 2020-01-01 00:00:00 |
| update_date | Long | - | - | - | - | 2020-01-01 00:00:00 |
| email_address | String | - | o | - | GoogleSignInAccount#email | test@example.com |
| access_token | String | - | - | - | TokenInfo#accessToken | - |
| refresh_token | String | - | - | - | TokenInfo#refreshToken | - |
| expired_access_token | Long | - | - | - | TokenInfo#expiredAccessTokenTimeMillis | 2020-01-01 00:00:00 |
| update_photos | Long | - | - | - | - | 2020-01-01 00:00:00 |

### SelectedAlbum DB

| TableName | Type | Primary | KeyT | Foreign | Note | Example |
|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
| id | Long | o | - | o | DisplayedPhotos#selected_album_id | 1 |
| create_date | Long | - | - | - | - | 2020-01-01 00:00:00 |
| update_date | Long | - | - | - | - | 2020-01-01 00:00:00 |
| user_id | Long | - | - | o | UserInfo#id | - |
| album_id | String | - | o | - | Album#id | - |
| album_title | String | - | - | - | Album#title | test_album |
| cover_media_item_id | String | - | - | - | Album#coverPhotoMediaItemId | - |

### DisplayedPhotos DB

| TableName | Type | Primary | KeyT | Foreign | Note | Example |
|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
| id | Long | o | - | - | - | 1 |
| create_date | Long | - | - | - | - | 2020-01-01 00:00:00 |
| update_date | Long | - | - | - | - | 2020-01-01 00:00:00 |
| selected_album_id | Long | - | - | o | SelectedAlbum#id | - |
| media_item_id | String | - | o | - | MediaItem#id | - |
| file_name | String | - | - | - | MediaItem#filename | test01.png |
| is_covered_album | Boolean | - | - | - | - | true |
