package std.wlj.solr;

import java.util.Iterator;
import java.util.Set;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.Metrics;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceResults.Annotation;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.DataMetrics;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.scoring.Scorer;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.PlaceRequestProfile;

import std.wlj.util.SolrManager;

/**
 * From Dan Shellman ...
 */
public class InterpTest {
    public static void main( String[] args ) throws Exception {
        PlaceRepresentation[]       interps = null;
        String[]                text = {
                //                      "orem, utah",
                //                      "new york, new york",
                //                      "paris, france",
                //                      ",Idaho",
                //                      "portland, or",
                //                      "London, England",
                //                      "Windsor, New South Wales",
                //                      "Taiamai",
                //                      "Saint-Martin-en-Bresse",
                //                      "Beit Mirrah, Lebanon",
                //                      "Le Noir, North Carolina",
                //                      "Lynn City 2, Essex, Massachusetts",
                //                      "Hessen Lande",
                //                      "Holland, , IN",
                //                      "Cork, Cork, Ireland",
                //                      "Baltimore,Md",
                //                      "Hidalgo co., Texas",
                //                      "Tart l'Abbaye, France",
                //                      "Spanish Fork Cemetery, Lot 6, Block 1, Position 7",
                //                      "Macau, Macau, China",
                //                      "Hung Mo, China",
                //                      "Baretswil, Zurich, Switzerland",
                //                      "Carthage Jail, Hancock, LI.",
                //                      "Salt Lake, Utah",
                //                      "Aguacalientes, Aguacalientes, Aguacalientes, Mexico",
                //                      ",,Aguacalientes, Mexico",
                //                      "Aguacalientes, Mexico",
                //                      "Jasper,, Georgia, United States",
                //                      "New Hampshire, USA",
                //                      "UT",
                //                      ",,UT",
                //                      "of UT",
                //                      "Of,Turkey",
                //                      ",,NH",
                //                      "Munich, Bavaria, Germany",
                //
                //                      "23 Assembly District 37 Precinct San Francisco City, San Francisco, California",
                //                      "160 South Warren Street",
                //                      "3- Portsmouth, New Hampshire",
                //                      "16th Ward Philadelphia",
                //                      "Aber, Scotland",
                //                      "Adjala, Cardwell, Ontario, Canada",
                //                      "Aguascalientes, Mexico", //barry
                //                      "Ajuchitlán del Progreso, Guerrero, Mexico",
                //                      "Albardón (Departamento), San Juan, Argentina",
                //                      "Ålborg, Denmark",
                //                      "All Saints, Newton(near Manchester), Lancashire, England",
                //                      "Alston, Cumberland, England",
                //                      "Alton Township Alton City Part Of 1, Madison, Illinois",
                //                      "Amparo, São Paulo, Brazil",
                //                      "Antrim, Ireland",
                //                      "Århus, Denmark",
                //                      "Ark",
                //                      "ASUNCION, MEXICO, DISTRITO FEDERAL, MEXICO",
                //                      "Atlantic",
                //                      "Atlixtac, Guerrero, Mexico",
                //                      "Australia",
                //                      "Austria",
                //                      "Ayr, Scotland",
                //                      "B...Son",
                //                      "Baden, Germany",
                //                      "Badiraguato, Sinaloa, Mexico",
                //                      "Barbados",
                //                      "Baumgarten (Ag. Butzow), Mecklenburg-Schwerin, Germany",
                //                      "Bayern, Germany",
                //                      "Beat 3 Ross Mill Precinct, Monroe, Mississippi",
                //                      "Bedford, England",
                //                      "BEENHAM, BERKSHIRE, ENGLAND",
                //                      "Belfast",
                //                      "BELFORT,HAUT-RHIN,FRANCE",
                //                      "Bengal, India",
                //                      "Berwick-upon-Tweed, Northumberland, England",
                //                      "Best,",
                //                      "Birth And Marriage Index,, Misc, New Hampshire",
                //                      "Bladåker, Stockholm, Sweden",
                //                      "Blekinge, Sweden",
                //                      "Bohemia",
                //                      "Boli, Bolivia",
                //                      "Bolton, Lancashire, England",
                //                      "Bornholm, Denmark",
                //                      "Boston",
                //
                //                      "Chelsea & Greenwood towns, Taylor, Wisconsin",
                //                      "King and Queen County, Virginia",
                //                      "Centro, Rio de Janeiro, Rio de Janeiro, Brazil",
                //                      "Morris Co New Jersey",
                //                      "Nuestra Señora de la Asunción, Córdoba, Córdoba, Argentina",
                //                      "Rhodt (BA. Landau), Bayern, Germany",
                //                      "3 Precinct Roxbury Boston City 16, Suffolk, Massachusetts",
                //                      "19th Precinct Chicago 10, Cook, Illinois",
                //                      "N. Wilkesboro, Wilkes, North Carolina",
                //                      "Santa Catarina, Rioverde, San Luis Potosi, Mexico",
                //                      "Santa Cruz y Soledad, Centro-Barrio la Soledad, Distrito Federal, Mexico",
                //                      ", ASARUM, BLEKINGE, SWEDEN",
                //                      "Baumgarten (Ag. Butzow), Mecklenburg-Schwerin, Germany",
                //                      "Benson Precinct, Cochise, Arizona Territory",
                //                      "Braaten tilh. Chrania Spiger & Valseværk",
                //                      "wyoming",
                //                      "BROADWATER BY WORTHING,SUSSEX,ENGLAND",
                //                      "Oaxaca de JuÃ¡rez, Oaxaca, Mexico",
                //                      "Santa MarÃ­a del Marquesado, Oaxaca de JuÃrez, Oaxaca, Mexico",
                //                      "Caivanos",
                //                      "Columbia county, Columbia, Georgia",
                //                      "Congress, Wayne, Ohio",
                //                      "Mich. U. S.",
                //                      "Norfolk Co., Va.",
                //                      "ward 3, Butte, South Dakota",
                //                      ", Dawson, Texas",
                //                      "12-Wd Scranton, Lackawanna, Pennsylvania",
                //                      "Burleigh & Anstruther, Ontario",
                //                      "Civil District 6 (east part), Dickson, Tennessee",
                //                      "DALTON IN FURNESS,LANCASHIRE,ENGLAND",
                //                      "W. Va.",
                //                      "Santo Stefano Quisqaina, Agrigento, Sicily, Italy",
                //                      "SANTA FE,GUANAJUATO,GUANAJUATO,MEXICO",
                //                      "SAN PABLO VILLA DE MITLA, OAXACA, MEXICO",
                //                      "Eden Township Mt Eden Precinct 1, Alameda, California",
                //                      "new york",
                //                      "New York City, ward 10, New York, New York",
                //                      "Decatur Township Decatur City 15 Precinct, Macon, Illinois",
                //                      "El Salvador, Valladolid, Valladolid, Spain",
                //                      "Fairview & Lake Townships, Monona, Iowa",
                //                      "West 1/2 Beat 1, Chombus, Alabama",
                //                      "Kansas Ward 11, Jackson, Missouri",
                //                      "provo",
                //                      "( centre Square, Montgromery, PA",
                //                      "stange s og pr",
                //                      "utah,pro",
                //                      "provo",
                //                      "ED 39 Justice Precinct 1 (all east of H.E.&W.T.R.R. excl. Nacogdoches city), Nacogdoches, Texas, United States",
                //                      "ED 20 Justice Precinct 1 (excl. Coleman city), Coleman, Texas, United States",
                //                      "*",
                //                      "United States,Mississippi,Calhoun",
                //                      "ark",
                //                      "Inmaculada Concepción, Villa Atamisqui, Santiago del Estero, Argentina",
                //                      "Nuestra Señora de la Merced, San Juan, San Juan, Argentina",
                //                      "Nuestra Senora de la Inmaculada Concepción, Villa Atamisqui, Santiago del Estero, Argentina",
                //                      "Nuestra Senora de la Inmaculada, Buenos Aires, Distrito Federal, Argentina",
                //                      "Nuestra Señora de los Remedios, Montecristo, Córdoba, Argentina",
                //                      "Nuestra Señora de la Encarnación, San Miguel de Tucumán, Tucumán, Argentina",
                //                      "Nuestra Señora del Carmen, Santiago del Estero, Santiago del Estero, Argentina",
                //                      "Santa Maria De La Asuncion,Santa Maria Del Rio,San Luis Potosi,Mexico",
                //                      "Nuestra Señora de la Asunción, Santa María del Rio, San Luis Potosí, Mexico",
                //                      "Santos Apostoles Felipe y Santiago, San Felipe del Progreso, Mexico, Mexico",
                //                      "Santo Antônio da Patrulha, Santo Antônio da Patrulha, Rio Grande do Sul, Brazil",
                //                      "Neustra Senora de la Asuncion, Santa Maria del Rio, San Luis Potosi, Mexico",
                //                      "an Juan Bautista,Zimapan,Hidalgo,Mexico",
                //                      "St. Paul's, London St. Martin Ludgate, London St. Brigide (Brides) London London St. Brides St. Sepulchre Newgate Bysshopesgate, London St. Brides St. Nicholas Coldabbey, London London St. Martin Ludgate, London St. Brides St. Martin Ludgate, London St. Se",
                //                      "Verschollen Auf See, mit dem Schiff \"Frauke Catharina\" auf einer Reise nach London, am 4.Feb. 1856 amtlich für Tot erklärt",
                //                      "Hoa Da Ap, Bao An Dong Tay Nhi Xa, Da Hoa Thuong Tong, Dien Phuoc Huyen, Dien Ban Phu, Quang Nam Tinh, Vietnam",
                //                      "paris,id",
                //                      "Hidalgo, Texas",
                //                      "台灣省雲林縣", //Yunlin, Taiwan
                //                      "бежаницкиы",
                //                      "Hawaii, Hawaii",
                //                      "provo,utah,utah",
                //                      "Aroostook, Maine",
                //                      "阿拉巴馬州", //Alabama in Traditional Chinese
                //                      "porto,portugal",
                //                      "espirito santo, brasil",

                //                      "england,united kingdom",
                //                      "yorks, eng",
                //                      "ontario",
                //                      "p",
                //                      "york, eng",
                //                      "san jose,ca",

                //                      "Antas de Ulla Lugo, Spain",
                //                      "A Pobra de Brollon",
                //                        "A Pobra del Brollon",
                //                        "A Pobra do Brollón",
                //                      "Elizabeth City, Pasquotank, North Carolina",
                //                      "el encino, brooks, texas",
                //                      "N. Y. State",
                //                      ", VIK I SOGN, SOGN OG FJORDANE, NORWAY",
                //                        "S Carolina",
                //                        "New York City, ward 10, New York, New York",
                //                        "san isidro, collpa de nor cinti, chuquisaca, bolivi",
                //                        "MT USA",
                //                        "DE USA",
                //                        "AK USA",
                //                        "AR USA",
                //                        "KY USA",
                //                        "LA USA",
                //                        "MA USA",
                //                        "MS USA",
                //                        "Antagnod (San Martino)",
                //                        "美國",
                //                        "SAN FELIPE DE JESUS, COLIMA, COLIMA, MEXICO",
                //                        "denver,co",
                //                        "woolsocken,ri"
                //                        "holland",
                //                        "ala",
                //                        "Not Stated",
                //                        "o",
                //                        "nyc",
                //                        "n y c",
                //                        "penna",
                //                        "P Is",
                //                        "Sam Sen, Thailand",
                //                        "Saen To, Muang Uttaradit, Uttaradit, Thailand",
                //                        "georgia",
                //                        "AZ",
                //                        "CO",
                //                        "IN",
                //                        "doesntexist,usa"
                //                        "california",
                //                        "prova~,ut",
                //                          "徳島県徳島市北常三島町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町",
                //                          "徳島県徳島市北常三島町町町町町町町町町町町町町町",
                //                          "Salt Lake City, Utah, Utah, United States",
                //                          "SAN FELIPE DE J*S",
                //                          "L*n, England",
                //                            "canada",
                //                        "London, Stbride, Fleet St, Middlesex, England",
                //                            "Alan's Camp, AZ",
                //                            "porto benin",
                //                            "Fran,France",
                //                            "England,United Kingdom",
                //                            "mexico,mexico",
                //                            "KUALA KUBU BARU.SELANGOR.MALAYSIA 新古毛.雪蘭莪",
                //                            "加拿大, 維多利亞 (VICTORIA, CANADA)",
                //                            "new*",
                //                            "Big Bear Park Post Office  San Bernardino California",
                //                        "大阪市南区安堂寺橋通",
                //                        "ㄊ　ㄞˊ",
                //                        "USS Robinson, Guantanamo Bay, Cuba",
                //                        "portland,m",
                //                        "portland,maine",
                //                        "ut,prov",
                //                        "new york, new",
                //                        "texas,f",
                //                        "pro",
                //                        "california",
                //                        "Salt Lake City, Salt Lake, Utah, United States",
                //                        "中国安徽省滁州市",
                //                        "lincoln, wv",
                //                        "San Juan Tehuistitlan, Mexico, Mexico",
                //                        "provo,utah territory",
                //                        "臺灣台東",
                //                        "臺灣台東?",
                //                        "pravo~, ut",
                //                        "paris",
                //                        "(Budock,+Cornwall?)",
                //                        "mesa cemetery, mesa, az",
                //                        "Salt Palace, Salt Lake City, Utah, Utah, United States",
                //                        "Army Airforce Base A1, Hoinching, China",
                //                        "Hoinching, China",
                //                        "Bethlehem Baptist Church Cemetery, Lumpkin, Georgia, United States",
                //                        "independence, Clay, Missouri",
                //                        "福島県？",
                //                        "warren in",
                //                        "fountain, in",
                //                        "fountain, ind",
                //                        "warren",
                //                        "Greeenup, Kentucky United States",
                //                        "Plymouth, ma",
                //                        "va",
                //                        "n iowa",
                //                        "chica",
                //                        "los an",
                //                        "sal",
                //                        "la laja",
                //                        "nossa senhora, dos prazeres, sao paulo,brazil",
                //                        "usa",
                //                        "va?",
                //                        "philadelpia~",
                //                        "Wasatch Lawn Memorial Park, East Millcreek, Salt Lake, Utah, United States",
                //                        "South America",
                //                        "Qu&eacute;bec,Canada",
                //                        "Salt Lake",
                //                        "4900 S 2000 W, Roy, Weber, Utah, Ut 84067, BUILDING A", //partial input
                //                        "438 1/2 Elm Way, Homestead Allegheny Pennsylvania United s", //partial input
                //                        "Berry Highland Memorial Cemetery",
                //                        "Pine Ridge Civic and Historical Cemetery",
                //                        "Pleasant Mills Cemetery",
                //                        "united s", //partial input
                //                        "Athens, Ohio,", //partial input
                //                        "Fort Utah",
                //                        "colony of virginia",
                //                        "British Colonial America",
                //                        "Sag Harbor, Suffolk, Colony of New York, British Colonial America",
                //                        "Santa Fe, Santa Fe, Nuevo México, Estados Unidos de América", //Not working correctly!
                //                        "アメリカ合衆国サウスカロライナ州", //United States, South Carolina
                //                        "アメリカ合衆国ハワイ州", //United States, Hawaii
                //                        "フィリピンレイテ島オルモック方面", //Ormoc, Leyte, Phillipines
                //                        "Las Vegas Nevada Temple, Las Vegas, Clark, Nevada, United States",
                //                        "Mar 20 2014",
                //                        "Norfolkshire, England, United Kingdom",
                //                        "イリノイ州シカゴ市", //Illinois State, Chicago City
                //                        "<Lowestoft Suffolk England>",
                //                        "(missing)",
                //                        "Isola Martena in Lago di Bolsena, (Present Provincia di Viterbo), Tuscia et Umbria (present Lazio), Italy (As a captive of her cousin Theodohad, strangled by his hirelings while bathing)",
                //                        "Kitchener ON",
                //                        "Pennsylbania~", //partial input
                //                        "Seabrook, Rock, NH, *See Notes",
                //                        "West Valley Utah",
                //                        "Flat Lick Cumberland Presbyterian Church Cemetery  Herndon Christian County Kentucky, USA GPS (lat/lon): 36.74588, -87.54647",
                //                        "Santa Clara Township, Campbell and Moreland Election Precincts (excl. Santa Clara town), Santa Clara, California, United States",
                //                        "New Whatcom~,Whatcom,Washington",
                //                        "oram,ut",
                //                        "Baptisms (Křty) 1900-1902 (v. 38), Catholic, České Budějovice, České Budějovice",
                //                        "U S A",
                //                        "Iditarod Area Regional Educational Attendance Area",
                //                        "New Jersey U S A",
                //                        "気仙郡～",
                //                        "気仙郡～タン",
                //                        "<blank>,provo,ut",
                //                        "Salt Lake City Utah",
                //                        "Esmeraldas-Esmeraldas-Ecuador"
                //                        "Birth Certificates, Manhattan, New York, New York",
                //                          "Populated Place, Western Cape, South Africa",
                //                          "27 Craven St Liverpool",
                //                          "N.Y. Manhattan",
                //                          "Washington D.C. Temple, Kensington, Montgomery, Maryland, United States",
                //                        "Delaware, marriage records: Marriage records, v. 1 p. 1-90, ca. 1744-1766",
                //                        "City of Wilmington marriage records, 1881-1954: Marriages, 1945",
                //                        "Boninne, Namur, Tables décennales, publications de mariage, mariages, naissances, décès 1901-1910",
                //                        "Baptisms (Křty) 1900-1902 (v. 38), Catholic, České Budějovice, České Budějovice",
                //                        "Naissances, publications de mariage, mariages, décès 1901, Namur, Waillet",
                //                        "Espinheiro, Nascimentos, Nascimentos 1938, Maio-1939, Abr, Pernambuco, Recife, null",
                //                        "Juliaca, Matrimonios 1975 ene-dic, San Román",
                //                        "Baptisms (Křty) 1862-1867 (v. 19), Catholic, Písek, Písek",
                //                        "益陽",
                //                        "[Blank], Salt Lake, Utah Territory, United States",
                //                        "Old OceanTexas",
                //                        "Surry County, North Carolina marriage licenses, ca. 1868-1961: Marriage licenses, 1868-1899, Ladd-Young",
                //                        "Richmond County, North Carolina marriage bonds, early to 1868: Also on microfilm. Salt Lake City : Filmed by the  Genealogical Society of Utah, 1966. on 1  microfilm reel ; 35 mm.",
                //                        "Pennsylvania, Philadelphia, marriage records: Marriages, 584900-584999, 1930",
                //                        "Marriage licenses (Mecklenburg County, North Carolina), 1851-1962: Marriage licenses, 1877-1880",
                //                        "Juliaca, Matrimonios 1962 ene-dic, San Román",
                //                        "Aberdeen,Chebalis,Washington",
                //                        "Republica de China",
                //                        "KentuckyUSA",
                //                        "New YorkUSA",
                //                        "Old Mexico",
                //                        "Palace of Westminster, London Westminster, Middlesex, England",
                //                        "Anacortes,Skagit,Washington",
                //                        "24 May 1902",
                //                        ",,VA Colony",
                //                        "N.Y. & N.J. Crematory",
                //                        "The South of Scotland",
                //                        "Gwinn Chapel African Methodist Episcopal Church, Marion, Ohio, United States",
                //                        "São Miguel, Nevogilde, Porto, Porto, Portugal",
                //                        "São Miguel, Porto, Porto, Portugal",
                //                        "1940 census Illinois",
                //                        "<of Salt Lake>",
                //                        "Estill, Kentucky",
                //                        "Colony of Virginia",
                //                        "Kingston On Canada",
                //                        "Bautismos 1949 oct-1962 mayo, Carabobo, Inmaculado Corazón de María, Valencia",
                //                        "Bautismos 1949 oct-1958 mayo (incluye una partida de 1959), Carabobo, Inmaculado Corazón de María, Valencia",
                //                        "salt lake,sandy,utah",
                //                        "Bautismos 1949 oct-1962 mayo, Carabobo, Valencia, Inmaculado Corazón de María",
                //                        "Bautismos 1964 sep-1974 dic, Carabobo, La Asunción y Santa Rita, Valencia",
                //                        "Bautismos 1930 apr-1932 mar, Carabobo, Nuestra Señora de la Candelaria, Valencia",
                //                        "Carabobo, La Asunción y Santa Rita, Matrimonios 1964 dic-1985 jul, Valencia",
                //                        "N.Y. & N.J. Cremation",
                //                        "Carabobo, Nuestra Señora de la Candelaria, Reposiciones de bautismos 1962-1975, vol 1, Valencia",
                //                        "Carabobo, Nuestra Señora de la Candelaria, Reposiciones de bautismos 1975, vol 2, Valencia",
                //                        "ogden,utah, weber",
                //                        "Norwich, Norfolk, England",
                //                        "ニカラグア", //Japanese for Nicaragua
                //                        "north da", //type ahead
                //                        "Birth, marriage and death register 1857 vol 1, Mahoning, (OH) Register of births, marriages, deaths, 1857 | Register of births, marriages, deaths, 1857",
                //                        "Camp B*, , Texas",
                //                        "Camp H??, , California",
                //                        "Cl, , North Carolina",
                //                        "El Centre, , California",
                //                        "Gl, , Illinois",
                //                        "Mac Dill, , Florida",
                //                        "Natal, , Brazil",
                //                        "S P, , California",
                //                        "Saint Louis, Saint Louis City,",
                //                        "Scott, , California",
                //                        ", Buchanan,",
                //                        "(123 Main St)",
                //                        "Montreal,Canada",
                //                        "Williamson, Illinois",
                //                        ",Franklin,Missouri",
                //                        "Holt,Missouri",
                //                        "Henderson, Texas",
                //                        "Guadalupe, Texas",
                //                        "Hamilton, Texas",
                //                        "Hemphill, Texas",
                //                        "Hardin, Texas",
                //                        "USS Kentucky, usa", //tests 'uss' as a prefix for a ship
                //                        "ｱﾒﾘｶ、ｻｳｽｶﾛﾗｲﾅ", //tests japanese comma in name (USA,South Carolina)
                //                        "ｻｳｽｶﾛﾗｲﾅ", //tests japanese comma in name (South Carolina)
                //                        "San Francisco (90)",
                //                        "56 Clarence Grove",
                //                        "aged 41",
                //                        "Trinitatis,Kobenhavn,Kobenhavn,Denmark",
                //                        "Sutton-by-Dover, Kent, England",
                //                        "日本, 岩手県, 胆沢郡, 金ヶ崎町",
                //                        "Belarus, Vitsebsk, Vitsebsk, Vitsebsk",
                //                        "Nuestra Se?? de la Piedad, Buenos Aires, Distrito Federal, Argentina",
                //                        "International Waters. International Waters, International Waters",
                //                        "Catedral Nuestra Señora de la Asunción, Asunción, Distrito Capital, Paraguay",
                //                        "Ukraine, Sumy, Lebedyn, Tokari", //Missed high relevance because too many results
                //                        "Germany, Hessen, Lampertheim", //Missed high relevance because too many results
                //                        "Rome, Lazio, Italy",
                //                        "HEADSTONE DAMAGED  DIED: FEB 23, 18??",
                //                        "baltimore,maryland",
                //                        "Waller, Florida, Confederate Veterans and Widows Pension Applications, 1885-1955-FSI",
                //                        "Atmore, Florida, Confederate Veterans and Widows Pension Applications, 1885-1955-FSI",
                //                        "Cagayan de Oro, Philippines",
                //                        "COLON HONDURA", //type ahead
                //                        "la iglesia parroquial de Nuestra Senora del Pilar y San Rafael del Cerro Largo, Melo, Cerro Largo, Uruguay",
                //                        ", , Pennsylvania, North Carolina, Discharge and Statement of Service Records, 1940-1948-FSI, Gaston",
                //                        "Westover Field Base,", //interp with parent (North Carolina)
                //                        "l*,PA",
                //                        "Naranjito, Puerto Rico",
                //                        "Sandate-General Church/ENG # # Sect, Australia",
                //                        "Justice Precinct 4 (all south of Mexia & Co. Gin Road & Mexia & R.C. Road), Limestone, Texas, United States",
                //                        "Montreal", //Common name should come back first in Canada
                //                        "Kings Co", //interp in canada in 1895
                //                        "Decatur, United States",
                //                        "Nord, Rwanda",
                //                        "(buried at Worsborough, England)",
                //                        "Haddeth el Joubbe, Lebanon", //STD-3632
                //                        "Augusta, Kennebec, Maine, United States", //STD-3633
                //                        "Election Districts 2 & 12 (incl. Salvation Army Rescue Home) St. Paul city Ward 9, Ramsey, Minnesota, United States",
                //                        "probably United States",
                //                        "Portland, Mo", //type-ahead
                //                        "Sampford-Exxec-England-UK",
                //                        "Ukraine, Mykolaïv, Nova Odesa, Malynivka",
                //                        "Russia, Vil′na, Lida, Dylevo",
                //                        "Ukraine, Dnipropetrovs′k, Novomoskovs′k, Voskresenivka",
                //                        "Україна, Львів, Городок, Мильчичі",
                //                        "Slovakia, Senec, Nové Košarinská",
                //                        "Argentina, Buenos Aires, Baradero (Partido)",
                //                        "Poland, Tarnopol, Szyszkowce (Brody)",
                //                        "Japan, Gifu-ken, Minokamo-shi, Kamono-chō",
                //                        "中國浙江杭州 鹿村", //embedded space
                //                        "Germany, Preußen, Ostpreußen, Adlig Crottingen", //Has out-of-order candidates:  affects interp call
                //                        "Germany, Preußen, Ostpreußen, Borkenwalde (Kr. Angerburg)",
                //                        "Knox Co., TN",
                //                        "<Baptisms (1900-2000), Utah>",
                //                        "<Baptisms, Utah>",
                //                        "<Barcelona, spain>",
                //                        "First Baptist Church of St. Alban", //type-ahead taking too long
                //                        "Kings",
                //                        "New Brunswick, Canada",
                //                        "276 west 1310 north orem, utah, usa (1900-2000)",
                //                        "Washington, United States", //for interp endpoint:  should get washington state
                //                        "日本国", //last character is 'country' in Japanese
                //                        "at Sea to Brazil", //handling 'to'
                //                        "Enroute to Paris", //handling 'to'
                //                        "Boston, MA", //no differentiator for modern versus historic
                //                        "Haddeth el Joubbe, Lebanon",
                //                        "廣州",
                //                        "Austria, Böhmen, Könighof an der Elbe, Silwarleut",
                //                        "Franklin (Independent City), Virginia, United States",
                //                        "ニカラグア", //Nicaraqua in Japanese
                //                        "Afghanistan, Balkh (province)",
                //                        "San Francisco,ca",
                //                        "New York City",
                //                        "Boston",
                //                        "us",
                //                        "NOT MARRIED (mistress)",
                //                        "Argentina, Buenos Aires, Baradero (Partido)",
                //                        "Harput, Hayastan-Turkey, Armenia",
                //                        "Cherry Valley, Otsego Co. New York, USA", //Low rel due to 'co.' abbr.
                //                        "of Dublin, Ireland.", //Low rel fixed to be high rel
                //                        "of Laimbach Parish, Melk, Lower Austria, Austria", //Low rel due to name variants
                //                        "Borsod, Deaths (Halottak) 1904-1906, Mezőkövesd",
                //                        "Alsóhangony, Births (Születtek) 1895-1908, Deaths (Halottak) 1895-1908, Gömör, Marriages (Házasultak) 1895-1902, Marriages (Házasultak) 1905-1908",
                //                        "Births (Születtek) 1895-1908, Borsod, Deaths (Halottak) 1895-1904, Deaths (Halottak) 1905-1908, Marriages (Házasultak) 1896-1908, Sajókazincz",
                //                        "Russia, Tver Church Books, 1722-1918 --> Смоленск --> Белой --> Комарово --> 1881-1887 Vol. 55 Births, marriages (Рождения, бракосочетания, смерти)",
                //                        "Aibonito County, Puerto Rico, USA",
                //                        "1852, 1864, Burials 1852-1864, Warwickshire, Leamington, All Saints' Church, Warwick, England",
                "England?",
                "Engl?and?",
        };
        long                    beginTime, totalTime, totalParseTime = 0, totalTailMatchTime = 0, totalTailFilterTime = 0;
        long                    newAverageTime = 0;
        float                   averageRelScore = 0;
        Set<Scorer>             scorers;
        //        Set<Filter>             filters;
        PlaceRequestBuilder     request = new PlaceRequestBuilder();
        PlaceService            service;
        PlaceResults            results;

//        service = PlaceService.getInstance( "default" );
        //        service = PlaceService.getInstance( "interp" );

        SolrService  solrService = SolrManager.localEmbeddedService("D:/solr/stand-alone-6.1.0");
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        service = new PlaceService(profile);

        service.getPlaceRepresentation( 1 ).getJurisdictionChain();
        System.out.println( "Starting interpretations..." );
        for ( int i = 0; i < text.length; i++ ) {
            int         relScoreTotal;
            float       aRelScore;

            beginTime = System.nanoTime();
            request = service.createRequestBuilder( text[ i ], new StdLocale( "en" ) );
            request.setFilterResults( false ).setShouldCollectMetrics( true );
            request.setResultsLimit( 10 );
            request.setCanModifyRequest( true );
            //          request.setFilterResults( true );
            //            request.setFuzzyType( PlaceRequest.FuzzyType.EDIT_DISTANCE );
            request.setUseWildcards( true );
            //            request.setFilterThreshold( 0 );
            request.setPartialInput( false );
            //            request.addRequiredPlaceType( PlaceType.getInstance( service.getProfile().getDataService(), 61 ) );
            //            request.addRequiredParent( service.getPlaceRepresentation( 5797, null ) );
            //            request.addRequiredParent( service.getPlaceRepresentation( 5798, null ) );
            //            request.addRequiredParent( service.getPlaceRepresentation( 5799, null ) );
            //            request.setOptionalDate( GenealogicalDate.getInstance( "1875" ) );
            //            request.addOptionalParent( service.getPlaceRepresentation( 132, null ) );
            //            request.addRequiredPlaceTypeGroup( TypeGroup.getInstance( service.getProfile().getDataService(), 29 ) );
            results = service.requestPlaces( request.getRequest() );
            //            results = service.interpretPlaceName( text[ i ], null, "New Brunswick, Canada", null, null, null );
            //            results = service.interpretPlaceName( text[ i ], null, null, null, null, null );
            interps = results.getPlaceRepresentations();
            totalTime = System.nanoTime() - beginTime;
            newAverageTime += totalTime;
            System.out.println( "|" + text[ i ] + "|" + totalTime );
            if ( results.getError() != null ) {
                results.getError().printStackTrace();
                service.shutdown();
                return;
            }
            //            scorers = results.getMetrics().getTimedScorers();
            //            for ( Scorer scorer : scorers ) {
            //                System.out.println( scorer.getClass().getSimpleName() + " time|" + results.getMetrics().getScorerTime( scorer ) );
            //            }
            //            filters = results.getMetrics().getTimedFilters();
            //            for ( Filter filter : filters ) {
            //                System.out.println( filter.getClass().getSimpleName() + " time|" + results.getMetrics().getFilterTime( filter ) );
            //            }

            //            System.out.println( "Initial candidate count|" + results.getMetrics().getRawCandidateCount() );
            //            System.out.println( "Pre-scoring candidate count|" + results.getMetrics().getPreScoringCandidateCount() );
            //            System.out.println( "Final candidate count|" + interps.length );
            //            System.out.println( "Number of Token Lookups|" + results.getMetrics().getNumberOfTokenLookups() );
            //            System.out.println( "Parse time|" + results.getMetrics().getParseTime() );
            //            System.out.println( "Parse (Pre-Process) time|" + results.getMetrics().getMetric( PlaceTextParser.METRIC_PRE_PROCESS_TIME ) );
            //            System.out.println( "Parse (Segmentation) time|" + results.getMetrics().getMetric( PlaceTextParser.METRIC_SEGMENTATION_TIME ) );
            //            System.out.println( "Parse (Path Creation) time|" + results.getMetrics().getMetric( PlaceTextParser.METRIC_PARSE_PATH_TIME ) );
            //            System.out.println( "Parse (Post Process) time|" + results.getMetrics().getMetric( PlaceTextParser.METRIC_POST_PROCESS_TIME ) );
            //            System.out.println( "Parse (Raw Token) count|" + results.getMetrics().getMetric( PlaceTextParser.METRIC_RAW_TOKEN_COUNT ) );
            //            System.out.println( "Parse (Initial Path) count|" + results.getMetrics().getMetric( PlaceTextParser.METRIC_INITIAL_PATH_COUNT ) );
            //            System.out.println( "Parse (Final Path) count|" + results.getMetrics().getMetric( PlaceTextParser.METRIC_FINAL_PATH_COUNT ) );
            totalParseTime += results.getMetrics().getSimpleNumberMetric( Metrics.SimpleNumberMetric.PARSE_TOTAL_TIME );
            totalTailMatchTime += results.getMetrics().getSimpleNumberMetric( Metrics.SimpleNumberMetric.IDENTIFY_TAIL_MATCH_TIME ) == null ? 0 : results.getMetrics().getSimpleNumberMetric( Metrics.SimpleNumberMetric.IDENTIFY_TAIL_MATCH_TIME );
            //            System.out.println( "Identify Candidate time|" + results.getMetrics().getIdentifyCandidatesTime() );
            //            System.out.println( "Identify Candidate (lookup) time|" + results.getMetrics().getIdentifyCandidateLookupTime() );
            //            System.out.println( "Identify Candidate (tail match) time|" + results.getMetrics().getIdentifyCandidateTailMatchTime() );
            //            System.out.println( "Identify Candidate (max hit filter) time|" + results.getMetrics().getIdentifyCandidateMaxHitFilterTime() );
            //            System.out.println( "Identify Candidate (filter out parse paths) time|" + results.getMetrics().getIdentifyCandidateFilterParsePathTime() );
            //            System.out.println( "Identify Candidate (annotation) time|" + results.getMetrics().getMetric( "identifyCandidatesAnnotationTime" ) );
            //            System.out.println( "Scoring time|" + results.getMetrics().getScoringTime() );
            //            System.out.println( "Assembly time|" + results.getMetrics().getAssemblyTime() );
            //            System.out.println( "Type Ahead time|" + results.getMetrics().getMetric( "typeAheadTime" ) );
            //            System.out.println( "Token Lookup time|" + results.getMetrics().getMetric( "tokenLookupTime" ) );
            //            System.out.println( "Type Ahead Parent Lookup time|" + results.getMetrics().getMetric( "typeAheadParentLookupTime" ) );
            //            System.out.println( "Type Ahead Child Lookup time|" + results.getMetrics().getMetric( "typeAheadChildLookupTime" ) );
            System.out.println( "Type Ahead Percentage|" + results.getMetrics().getSimpleNumberMetric( Metrics.SimpleNumberMetric.TYPE_AHEAD_PARENT_PERCENTAGE ) );
            System.out.println( "Type Ahead Little Endians found: " + results.getMetrics().getSimpleNumberMetric( Metrics.SimpleNumberMetric.TYPE_AHEAD_LITTLE_END_COUNT ) );
            System.out.println( "Type Ahead Big Endians found: " + results.getMetrics().getSimpleNumberMetric( Metrics.SimpleNumberMetric.TYPE_AHEAD_BIG_END_COUNT ) );
            System.out.println( "Type Ahead Unknown Endians found: " + results.getMetrics().getSimpleNumberMetric( Metrics.SimpleNumberMetric.TYPE_AHEAD_NO_DIRECTION_COUNT ) );
            //            System.out.println( "Initial ParsedInputText count|" + results.getMetrics().getInitialParsedInputTextCount() );
            //            System.out.println( "Final ParsedInputText count|" + results.getMetrics().getFinalParsedInputTextCount() );
            System.out.println( "Relevance score adjustment|" + results.getMetrics().getSimpleNumberMetric( Metrics.SimpleNumberMetric.RELEVANCE_ADJUSTMENT ) );
            Iterator<Annotation> annotations = results.getAnnotations();
            while ( annotations.hasNext() ) {
                System.out.println( "Annotation: " + annotations.next().getName() );
            }
            relScoreTotal = 0;
            for ( int j = 0; j < interps.length; j++ ) {
                relScoreTotal += interps[ j ].getMetadata().getInterpretation().getScorecard().getRelevanceScore();
                scorers = interps[ j ].getMetadata().getInterpretation().getScorecard().getScorersThatScored();
                System.out.println( constructIdChain( interps[ j ] ) + "|" + interps[ j ].getFullDisplayName( new StdLocale( "en" ) ).get() + "|" + interps[ j ].getMetadata().getInterpretation().getParsedInput().toNormalizedString() );
                System.out.println( "DEBUG: " + interps[ j ].getMetadata().getInterpretation().toString() );
                //              System.out.println( "DEBUG: " + interps[ j ].getMetadata().getInterpretation().getParsedInput().toNormalizedString() );
                //              System.out.println( interps[ j ].getFullPreferredDisplayName().get() + " (" + interps[ j ].getMetadata().getInterpretation().getScorecard().getRawScore() + ")" );
                System.out.println( "Raw/Relevance Score|" + interps[ j ].getMetadata().getScoring().getRawScore() + "|" + interps[ j ].getMetadata().getScoring().getRelevanceScore() );
                System.out.println( "Detect Lang|" + interps[ j ].getMetadata().getInterpretation().getParsedInput().getPathLanguage() );
                for ( Scorer scorer : scorers ) {
                    String       reason = "";
                    int          basisScore;

                    reason = interps[ j ].getMetadata().getInterpretation().getScorecard().getScoreReason( scorer );
                    basisScore = results.getMetrics().getMapNumberMetric( Metrics.MapNumberMetric.SCORER_BASIS_SCORE, scorer.getClass().getSimpleName() ).intValue();
                    System.out.println( "     " + scorer.getClass().getSimpleName() + " (" + basisScore + "): " + interps[ j ].getMetadata().getInterpretation().getScorecard().getScoreFromScorer( scorer ) + " (" + reason + ")" );
                }
                //              for ( ParsedToken token : interps[ j ].getMetadata().getInterpretation().getParsedInput().getTokens() ) {
                //                  if ( token.getPlaceTypes() != null && token.getPlaceTypes().length > 0 ) {
                //                      System.out.print( "Token " + token.getOriginalToken() + ": " );
                //                      for ( int k = 0; k < token.getPlaceTypes().length; k++ ) {
                //                          System.out.print( token.getPlaceTypes()[ k ] + "," );
                //                      }
                //                      System.out.println( "" );
                //                  }
                //                  else {
                //                      System.out.println( "No types found in token: " + token.getOriginalToken() );
                //                  }
                //
                //              }
            }
            if ( interps.length > 0 ) {
                aRelScore = relScoreTotal / interps.length;
            }
            else {
                aRelScore = 0;
            }
            averageRelScore += aRelScore;
            //            System.out.println( "Interpretations removed:" );
            //            Set<Object> mapKeys = results.getMetrics().getMapObjectMetricSet( Metrics.MapObjectMetric.CANDIDATE_REMOVAL_REASON );
            //            for ( Object obj : mapKeys ) {
            //                Interpretation    interp = ( Interpretation ) obj;
            //                System.out.println( "REMOVED: " + interp.toString() + " for reason " + results.getMetrics().getMapObjectMetric( Metrics.MapObjectMetric.CANDIDATE_REMOVAL_REASON, interp ) );
            //            }
            interps = results.getAlternatePlaceRepresentations();
            if ( interps != null && interps.length > 0 ) {
                int         altRelScoreTotal = 0;

                System.out.println( "Alternative Places Found:");
                for ( int j = 0; j < interps.length; j++ ) {
                    altRelScoreTotal += interps[ j ].getMetadata().getInterpretation().getScorecard().getRelevanceScore();
                    scorers = interps[ j ].getMetadata().getInterpretation().getScorecard().getScorersThatScored();
                    System.out.println( constructIdChain( interps[ j ] ) + "|" + interps[ j ].getFullDisplayName( new StdLocale( "en" ) ).get() + "|" + interps[ j ].getMetadata().getInterpretation().getParsedInput().toNormalizedString() );
                    System.out.println( "DEBUG: " + interps[ j ].getMetadata().getInterpretation().toString() );
                    System.out.println( "Raw/Relevance Score|" + interps[ j ].getMetadata().getScoring().getRawScore() + "|" + interps[ j ].getMetadata().getScoring().getRelevanceScore() );
                    for ( Scorer scorer : scorers ) {
                        String       reason = "";
                        int          basisScore;

                        reason = interps[ j ].getMetadata().getInterpretation().getScorecard().getScoreReason( scorer );
                        basisScore = results.getMetrics().getMapNumberMetric( Metrics.MapNumberMetric.SCORER_BASIS_SCORE, scorer.getClass().getSimpleName() ).intValue();
                        System.out.println( "     " + scorer.getClass().getSimpleName() + " (" + basisScore + "): " + interps[ j ].getMetadata().getInterpretation().getScorecard().getScoreFromScorer( scorer ) + " (" + reason + ")" );
                    }
                }
                if ( aRelScore == 0 ) {
                    averageRelScore += altRelScoreTotal / interps.length;
                }
            }
        }

        System.gc();
        System.out.println( "|Total Time|" + newAverageTime );
        newAverageTime = newAverageTime / text.length;
        System.out.println( "|Average Time (nano)|" + newAverageTime );
        System.out.println( "|Used Memory|" + ( ( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() ) / 1024 ) );
        System.out.println( "|Average parse time|" + ( totalParseTime / text.length ) );
        System.out.println( "|Average tail match time|" + ( totalTailMatchTime / text.length ) );
        System.out.println( "|Average tail match filter (max hits) time|" + ( totalTailFilterTime / text.length ) );
        System.out.println( "|Average Relevance Score|" + ( averageRelScore / text.length ) );

        DataMetrics     metrics;

        metrics = service.getProfile().getDataService().getMetrics();
        //        System.out.println( "Warm cache hits: " + metrics.getNamedMetric( "WarmCacheHits" ).getValue() );
        //        System.out.println( "L1 cache hits: " + metrics.getNamedMetric( "L1CacheHits" ).getValue() );
        //        System.out.println( "L2 cache hits: " + metrics.getNamedMetric( "L2CacheHits" ).getValue() );
        //        System.out.println( "L3 cache hits: " + metrics.getNamedMetric( "L3CacheHits" ).getValue() );
        //        System.out.println( "Warm cache size: " + metrics.getNamedMetric( "WarmCacheSize" ).getValue() );
        //        System.out.println( "L1 cache size: " + metrics.getNamedMetric( "L1CacheSize" ).getValue() );
        //        System.out.println( "L2 cache size: " + metrics.getNamedMetric( "L2CacheSize" ).getValue() );
        //        System.out.println( "L3 cache size: " + metrics.getNamedMetric( "L3CacheSize" ).getValue() );
        System.out.println( "Doc Bank size: " + metrics.getNamedMetric( "DocBankSize" ).getValue() );
        System.out.println( "Doc Bank hit count: " + metrics.getNamedMetric( "DocBankHitCount" ).getValue() );

        service.shutdown();
    }

    private static String constructIdChain( PlaceRepresentation place ) {
        StringBuffer    buf = new StringBuffer();

        buf.append( "'" );
        for ( int id : place.getJurisdictionChainIds() ) {
            buf.append( id );
            buf.append( "," );
        }
        buf.deleteCharAt( buf.length() - 1 );
        buf.append( "'" );

        //Add the place chain
        buf.append( " (place: " );
        for ( PlaceRepresentation rep : place.getJurisdictionChain() ) {
            buf.append( rep.getPlaceId() );
            buf.append( "," );
        }
        buf.deleteCharAt( buf.length() - 1 );
        buf.append( ")" );

        return buf.toString();
    }


}
