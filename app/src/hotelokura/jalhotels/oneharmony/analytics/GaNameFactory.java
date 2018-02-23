package hotelokura.jalhotels.oneharmony.analytics;

import hotelokura.jalhotels.oneharmony.activity.catalog.CatalogToolbarView;

public class GaNameFactory {

    public static GaScreenName getNameFromMenu(String prefix, int index, int mode, String id, String url, String content_title) {
        return new GaNameFromMenu(prefix, index, mode, id, url, content_title);
    }

    public static GaScreenName getNameFromCatalog(String prefix, int page) {
        return new GaNameFromCatalog(prefix, page);
    }

    public static GaScreenName getNameFromMaplist(String prefix, String text) {
        return new GaNameFromMapList(prefix, text);
    }

    public static GaScreenName getNameFromCatalogToolbar(String catalogId, int page, CatalogToolbarView.ButtonId buttonId) {

        GaScreenName gaName = null;
        switch(buttonId) {
            case contents:
                break;
            case thumbnail:
                break;
            case bookmark:
                break;
            case sns:
                break;
            case cart:
                gaName = new GaNameFromCTCart(catalogId, page);
                break;
            case building:
                break;
            case openTo:
                gaName = new GaNameFromCTLink(catalogId, page);
                break;
            case map:
                gaName = new GaNameFromCTMap(catalogId, page);
                break;
        }

        return gaName;
    }

    public static GaScreenName getNameFromWebtop(String prefix, int mode, String id, String url, String content_tilte) {
        return new GaNameFromWebtop(prefix, mode, id, url, content_tilte);
    }

    public static GaScreenName getNameFromNews(String category, String title) {
        return new GaNameFromNews(category, title);
    }

    /**
     * GAに送信する画面名生成ロジックの基底クラス
     */
    public static abstract class GaScreenName {
        protected String mName = null;

        public String getScreenName() {
            if (this.mName != null) {
                return this.mName;
            } else {
                return "";
            }
        }

        protected void setName(String name) {
            this.mName = name;
        }
    }

    /**
     * GAに送信する画面名生成ロジック（メニュー用）
     */
    public static class GaNameFromMenu extends GaScreenName {

        public GaNameFromMenu(String prefix, int index, int mode, String id, String url, String content_tilte) {
            int displayNum = index + 1;

            StringBuffer sb =
                    new StringBuffer(prefix)
                            .append(displayNum)
                            .append("_");

            switch (mode) {
                case 40: // url
                case 41: // url
                    sb.append("url[" + url + "]");
                    break;
                case 1: // catalog
                    sb.append("catalog[" + id + "]");
                    break;
                case 20: // movie
                    sb.append("movie");
                    break;
                case 30: // [map]
                case 31: // [list of map]
                    if (id != null && !id.isEmpty()) {
                        sb.append("map[" + id + "]");
                    } else if (content_tilte != null && !content_tilte.isEmpty()) {
                        sb.append("map[" + content_tilte + "]");
                    } else  {
                        sb.append("map");
                    }
                    break;
                case 10: // menu(catalog list)
                    if (id != null && !id.isEmpty()) {
                        sb.append("list[" + id + "]");
                    } else if (content_tilte != null && !content_tilte.isEmpty()) {
                        sb.append("list[" + content_tilte + "]");
                    } else {
                        sb.append("list");
                    }
                    break;
                case 50:
                    sb.append("news");
                    break;
            }
            this.setName(sb.toString());
        }
    }

    /**
     * GAに送信する画面名生成ロジック（カタログ用）
     */
    public static class GaNameFromCatalog extends GaScreenName {
        public GaNameFromCatalog(String prefix, int page) {
            StringBuffer sb =
                    new StringBuffer(prefix)
                            .append("[" + page + "]");
            this.setName(sb.toString());
        }
    }

    /**
     * GAに送信する画面名生成ロジック（マップリスト用）
     */
    public static class GaNameFromMapList extends GaScreenName {
        public GaNameFromMapList(String prefix, String text) {
            StringBuffer sb =
                    new StringBuffer(prefix)
                            .append("[" + text + "]");
            this.setName(sb.toString());
        }
    }

    /**
     * GAに送信する画面名生成ロジック（カタログ内ツールバー用）
     */
    public static abstract class GaNameFromCatalogToolbar extends GaScreenName {}

    public static class GaNameFromCTLink extends GaNameFromCatalogToolbar {
        public GaNameFromCTLink(String catalogId, int page) {
            StringBuffer sb =
                    new StringBuffer(catalogId + "_catalog_page[")
                            .append(String.valueOf(page))
                            .append("]_click_link");
            this.setName(sb.toString());
        }
    }

    public static class GaNameFromCTCart extends GaNameFromCatalogToolbar {
        public GaNameFromCTCart(String catalogId, int page) {
            StringBuffer sb =
                    new StringBuffer(catalogId + "_catalog_page[")
                            .append(String.valueOf(page))
                            .append("]_click_cart");
            this.setName(sb.toString());
        }
    }

    public static class GaNameFromCTMap extends GaNameFromCatalogToolbar {
        public GaNameFromCTMap(String catalogId, int page) {
            StringBuffer sb =
                    new StringBuffer(catalogId + "_catalog_page[")
                            .append(String.valueOf(page))
                            .append("]_click_map");
            this.setName(sb.toString());
        }
    }

    /**
     * GAに送信する画面名生成ロジック（usewebtopがtrueの画面）
     */
    public static class GaNameFromWebtop extends GaScreenName {
        public GaNameFromWebtop(String prefix, int mode, String id, String url, String content_tilte) {

            StringBuffer sb =
                    new StringBuffer(prefix)
                            .append("_menu_");

            switch (mode) {
                case 40: // url
                case 41: // url
                    sb.append("url[" + url + "]");
                    break;
                case 1: // catalog
                    sb.append("catalog[" + id + "]");
                    break;
                case 20: // movie
                    sb.append("movie");
                    break;
                case 30: // [map]
                case 31: // [list of map]
                    if (id != null && !id.isEmpty()) {
                        sb.append("map[" + id + "]");
                    } else if (content_tilte != null && !content_tilte.isEmpty()) {
                        sb.append("map[" + content_tilte + "]");
                    } else {
                        sb.append("map");
                    }
                    break;
                case 10: // menu(catalog list)
                    if (id != null && !id.isEmpty()) {
                        sb.append("list[" + id + "]");
                    } else if (content_tilte != null && !content_tilte.isEmpty()) {
                        sb.append("list[" + content_tilte + "]");
                    } else {
                        sb.append("list");
                    }
                    break;
                case 50:
                    sb.append("news");
                    break;
            }
            this.setName(sb.toString());
        }
    }

    public static class GaNameFromNews extends GaScreenName {
        public GaNameFromNews(String category, String title) {
            StringBuffer sb =
                    new StringBuffer("news_");
            if(category != null && !category.isEmpty()) {
                sb.append(category)
                  .append("_");
            }
            sb.append(title);
            this.setName(sb.toString());
        }
    }
}
