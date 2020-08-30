## Database design

### Base Interface

| TableName | Type | PrimaryKeys | ForeignKeys | Note | Example |
|:-:|:-:|:-:|:-:|:-:|:-:|
| id | Long | o | - | AutoGenerate | 1 |
| create_date | Long | - | - | System.currentTimeMillis() | 2020-01-01 00:00:00 |
| update_date | Long | - | - | System.currentTimeMillis() | 2020-01-01 00:00:00 |

### UserInfo DB

| TableName | Type | PrimaryKey | UniqueKeys | ForeignKeys | Note | Example |
|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
| id | Long | o | - | A | AutoGenerate | 1 |
| create_date | Long | - | - | - | System.currentTimeMillis() | 2020-01-01 00:00:00 |
| update_date | Long | - | - | - | System.currentTimeMillis() | 2020-01-01 00:00:00 |
| email_address | String | - | o | - | GoogleSignInAccount#email | test@example.com |
| token_info | String | - | - | - | TokenInfo (JSON) | - |
| last_updated_photos_date | Long | - | - | - | System.currentTimeMillis() | 2020-01-01 00:00:00 |

### SelectedAlbum DB

| TableName | Type | PrimaryKey | UniqueKeys | ForeignKeys | Note | Example |
|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
| id | Long | o | - | B | AutoGenerate | 1 |
| create_date | Long | - | - | - | System.currentTimeMillis() | 2020-01-01 00:00:00 |
| update_date | Long | - | - | - | System.currentTimeMillis() | 2020-01-01 00:00:00 |
| user_info_id | Long | - | o | a | UserInfo#id | 1 |
| album_id | String | - | o | - | Album#id | - |
| album | String | - | - | - | Album (JSON) | - |

### DisplayedPhotos DB

| TableName | Type | PrimaryKey | UniqueKeys | ForeignKeys | Note | Example |
|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
| id | Long | o | - | - | AutoGenerate | 1 |
| create_date | Long | - | - | - | System.currentTimeMillis() | 2020-01-01 00:00:00 |
| update_date | Long | - | - | - | System.currentTimeMillis() | 2020-01-01 00:00:00 |
| selected_album_id | Long | - | o | b | SelectedAlbum#id | 1 |
| media_item_id | String | - | o | - | MediaItem#id | - |
| media_item | String | - | - | - | MediaItem (JSON) | - |
| output_path | String | - | - | - | Output file path | xxx/yyy/zzz.png |
| is_my_favorite | Boolean | - | - | - | User favorite | true |
