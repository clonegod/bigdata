class GoogleAsset(object):
    table_name = "stg_gg_asset"

    fields = [
        "resource_name",
        "asset_id",
        "name",
        "type",
        "youtube_video_asset",
        "media_bundle_asset",
        "image_asset",
        "text_asset"
    ]


GoogleAsset = GoogleAsset()

if __name__ == "__main__":
    print(GoogleAsset.table_name)
    print(GoogleAsset.fields)
