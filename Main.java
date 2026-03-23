import static java.lang.System.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Deflater;
import java.util.regex.*;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.color.ColorSpace;
import javax.imageio.ImageIO;


public class Main {

    public static void main(String[] args) throws IOException{
        //Color Pallet Creation
        BufferedReader br = new BufferedReader(new FileReader("Colors.txt"));
        int n = Integer.parseInt(br.readLine());
        NamedColor[] pallet = new NamedColor[n];
        for(int i = 0; i<n; i++) {
            String[] data = br.readLine().split(" ");
            pallet[i] = new NamedColor(data[0], Integer.parseInt(data[1],16));
        }
        br.close();

        //File Name Input
        Scanner sc = new Scanner(in);
        out.println("Enter image file name: ");
        BufferedImage img = ImageIO.read(new File("Inputs/"+sc.nextLine()));
        out.println("Enter output file name: ");
        String outPath = "Outputs/"+sc.nextLine();
        sc.close();

        //Image Reading
        out.println("Proccessing...");
        int w = img.getWidth()/64*64, h = img.getHeight()/64*64;//Images must be multples of 64 pixels in width and height so extra pixels are cropped
        ArrayList<String> tileMap = new ArrayList<>(); 
        ArrayList<Integer>[] tileArray = new ArrayList[h];
        ArrayList<Integer>[] tileAmounts = new ArrayList[h];
        ArrayList<Integer> frozen_tiles = new ArrayList<Integer>();
        int tileNo = 0; //this is only used to keep track of frozen tiles because frozen tiles were added to this program after everything else
        Pattern frozen = Pattern.compile("\\?frozen");
        for(int i = 0; i<h; i++) {
            tileArray[i] = new ArrayList<Integer>();
            tileAmounts[i] = new ArrayList<Integer>();
            int[] rgbArray = img.getRGB(0, h-i-1, w, 1, null, 0, w);
            int previous = -1;//See Tile Mapping
            for(int rgb: rgbArray) {
                //Color Assignment
                Color c = new Color(rgb);
                double minDif = 1000000000.0;
                String name = "";
                for(NamedColor candidate: pallet) {
                    double dif = ciede2000(c, candidate);
                    if(dif<minDif) {
                        minDif = dif;
                        name = candidate.getName();
                        if(minDif==0) break;
                    }
                }

                //Freezing
                if(frozen.matcher(name).find()) {
                    frozen_tiles.add(tileNo);
                    name = name.substring(0,name.indexOf('?'));
                } /*tileNo is increased at the end of this loop*/
                
                //Adiing Quotes
                name = "\""+name+"\"";//name needs quotes because worldbox files have them around tile type names

                //Tile Mapping
                int index = tileMap.indexOf(name);
                if(index==-1) {
                    index = tileMap.size();
                    tileMap.add(name);
                }
                if(previous==index) tileAmounts[i].add(tileAmounts[i].removeLast()+1);
                else{
                    tileArray[i].add(index);
                    tileAmounts[i].add(1);
                    previous = index;
                }

                tileNo++;
            }
        }

        //Formatting & Output
        byte[] input = String.format("{\"saveVersion\":17,\"width\":%d,\"height\":%d,\"hotkey_tabs_data\":{},\"camera_pos_x\":128.0,\"camera_pos_y\":128.0,\"camera_zoom\":281.0,\"mapStats\":{\"name\":\"Generated World\",\"description\":\"\",\"player_name\":\"Benjamin Netanyahu\",\"player_mood\":\"serene\",\"custom_data\":{},\"world_time\":0.0,\"history_current_year\":-1,\"world_age_id\":\"age_hope\",\"current_world_ages_duration\":2820.0,\"current_age_progress\":0.0,\"world_ages_slots\":[\"age_hope\",\"age_dark\",\"age_hope\",\"age_unknown\",\"age_hope\",\"age_hope\",\"age_tears\",\"age_unknown\"],\"life_dna\":2026030918},\"worldLaws\":{\"list\":[{\"name\":\"world_law_spread_trees\"},{\"name\":\"world_law_spread_fungi\"},{\"name\":\"world_law_spread_plants\"},{\"name\":\"world_law_spread_fast_trees\",\"boolVal\":false},{\"name\":\"world_law_spread_fast_fungi\",\"boolVal\":false},{\"name\":\"world_law_spread_fast_plants\",\"boolVal\":false},{\"name\":\"world_law_spread_density_high\",\"boolVal\":false},{\"name\":\"world_law_exploding_mushrooms\",\"boolVal\":false},{\"name\":\"world_law_entanglewood\"},{\"name\":\"world_law_bark_bites_back\",\"boolVal\":false},{\"name\":\"world_law_plants_tickles\",\"boolVal\":false},{\"name\":\"world_law_root_pranks\",\"boolVal\":false},{\"name\":\"world_law_nectar_nap\",\"boolVal\":false},{\"name\":\"world_law_gene_spaghetti\",\"boolVal\":false},{\"name\":\"world_law_mutant_box\",\"boolVal\":false},{\"name\":\"world_law_glitched_noosphere\",\"boolVal\":false},{\"name\":\"world_law_drop_of_thoughts\",\"boolVal\":false},{\"name\":\"world_law_diplomacy\"},{\"name\":\"world_law_rites\"},{\"name\":\"world_law_peaceful_monsters\",\"boolVal\":false},{\"name\":\"world_law_hunger\"},{\"name\":\"world_law_vegetation_random_seeds\"},{\"name\":\"world_law_roots_without_borders\",\"boolVal\":false},{\"name\":\"world_law_grow_minerals\"},{\"name\":\"world_law_grow_grass\"},{\"name\":\"world_law_biome_overgrowth\"},{\"name\":\"world_law_terramorphing\"},{\"name\":\"world_law_kingdom_expansion\"},{\"name\":\"world_law_old_age\"},{\"name\":\"world_law_animals_spawn\"},{\"name\":\"world_law_animals_babies\"},{\"name\":\"world_law_rebellions\"},{\"name\":\"world_law_border_stealing\"},{\"name\":\"world_law_erosion\"},{\"name\":\"world_law_forever_lava\"},{\"name\":\"world_law_forever_cold\"},{\"name\":\"world_law_disasters_nature\"},{\"name\":\"world_law_clouds\"},{\"name\":\"world_law_evolution_events\"},{\"name\":\"world_law_disasters_other\"},{\"name\":\"world_law_rat_plague\",\"boolVal\":false},{\"name\":\"world_law_angry_civilians\",\"boolVal\":false},{\"name\":\"world_law_civ_babies\"},{\"name\":\"world_law_civ_migrants\"},{\"name\":\"world_law_forever_tumor_creep\"},{\"name\":\"world_law_civ_army\"},{\"name\":\"world_law_civ_limit_population_100\",\"boolVal\":false},{\"name\":\"world_law_gaias_covenant\",\"boolVal\":false},{\"name\":\"world_law_cursed_world\",\"boolVal\":false},{\"name\":\"age_hope\"},{\"name\":\"age_sun\"},{\"name\":\"age_dark\"},{\"name\":\"age_tears\"},{\"name\":\"age_moon\"},{\"name\":\"age_chaos\"},{\"name\":\"age_wonders\"},{\"name\":\"age_ice\"},{\"name\":\"age_ash\"},{\"name\":\"age_despair\"},{\"name\":\"age_unknown\"}]},\"tileMap\":%s,\"tileArray\":%s,\"tileAmounts\":%s,\"fire\":[],\"conwayEater\":[],\"conwayCreator\":[],\"frozen_tiles\":%s,\"tiles\":[],\"cities\":[],\"actors_data\":[],\"buildings\":[],\"kingdoms\":[],\"clans\":[],\"alliances\":[],\"wars\":[],\"plots\":[],\"relations\":[],\"cultures\":[],\"books\":[],\"subspecies\":[],\"languages\":[],\"religions\":[],\"families\":[],\"armies\":[],\"items\":[]}",w/64,h/64,tileMap.toString(),Arrays.toString(tileArray),Arrays.toString(tileAmounts),frozen_tiles.toString()).getBytes(StandardCharsets.UTF_8);
        DeflaterOutputStream dos = new DeflaterOutputStream(new FileOutputStream(new File(outPath)), new Deflater(Deflater.BEST_COMPRESSION));
        dos.write(input);
        dos.close();
        out.println("Done!");
    }

    public static double ciede2000(Color col1, Color col2)/*The comments are based on steps from a paper by Gaurav Sharma, Wencheng Wu, & Edul N. Dalal*/ {
        //Getting Lab values
        float[] lab1 = toLab(col1);
        float[] lab2 = toLab(col2);
        double[] lStar = {lab1[0],lab2[0]};
        double[] aStar = {lab1[1],lab2[1]};
        double[] bStar = {lab1[2],lab2[2]};

        //Calculation of C' and h'
        double cStarAB[] = new double[2];
        for(byte i = 0; i<2; i++) cStarAB[i] = Math.sqrt(aStar[i]*aStar[i]+bStar[i]*bStar[i]);
        double cBarStarAB = (cStarAB[0]+cStarAB[1])/2.0;
        double g = 0.5*(1.0-Math.sqrt(Math.pow(cBarStarAB,7.0)/(Math.pow(cBarStarAB,7.0)+Math.pow(25.0,7.0))));
        double[] aPrime = new double[2];
        double[] cPrime = new double[2];
        double[] hPrime = new double[2];
        for(byte i = 0; i<2; i++) {
            aPrime[i] = (1.0+g)*aStar[i];
            cPrime[i] = Math.sqrt(aPrime[i]*aPrime[i]+bStar[i]*bStar[i]);
            hPrime[i] = (bStar[i]==aPrime[i] && bStar[i]==0) ? 0.0 : Math.toDegrees(Math.atan2(bStar[i],aPrime[i]));
            if(hPrime[i]<0) hPrime[i]+=360.0;
        }

        //Calculation of deltaL', deltaC', and deltaH'
        double delta_LPrime = lStar[1]-lStar[0];
        double delta_CPrime = cPrime[1]-cPrime[0];
        double cPrimeProduct = cPrime[0]*cPrime[1];
        double delta_hPrime = hPrime[1]-hPrime[0];
        if(cPrimeProduct==0) delta_hPrime = 0;
        else if(delta_hPrime>180) delta_hPrime-=360;
        else if(delta_hPrime<-180) delta_hPrime+=360;
        double delta_HPrime = 2.0*Math.sqrt(cPrimeProduct)*Math.sin(Math.toRadians(delta_hPrime/2.0));

        //Calculation of CIEDE2000 Color-Difference deltaE
        double lBarPrime = (lStar[0]+lStar[1])/2.0;
        double cBarPrime = (cPrime[0]+cPrime[1])/2.0;
        double hBarPrime = hPrime[0]+hPrime[1];
        double hPrimeDistance = Math.abs(hPrime[0]-hPrime[1]);
        if(cPrimeProduct!=0.0) {
            if(hPrimeDistance>180.0) {
                if(hBarPrime<360.0) hBarPrime+=360.0;
                else hBarPrime-=360.0;
            }
            hBarPrime/=2.0;
        }
        double t = 1.0-0.17*Math.cos(Math.toRadians(hBarPrime-30.0))+0.24*Math.cos(Math.toRadians(2.0*hBarPrime))+0.32*Math.cos(Math.toRadians(3.0*hBarPrime+6.0))-0.2*Math.cos(Math.toRadians(4*hBarPrime-63.0));
        double deltaTheta = 30.0*Math.exp(-1.0*Math.pow((hBarPrime-275.0)/25.0, 2.0));
        double rc = 2.0*Math.sqrt(Math.pow(cBarPrime,7.0)/(Math.pow(cBarPrime, 7.0)+Math.pow(25.0, 7.0)));
        double squareOfLBarPrimeMinus50 = Math.pow(lBarPrime-50.0, 2.0);
        double sl = 1.0+(0.015*squareOfLBarPrimeMinus50)/Math.sqrt(20.0+squareOfLBarPrimeMinus50);
        double sc = 1.0+0.045*cBarPrime;
        double sh = 1.0+0.015*cBarPrime*t;
        double rt = -1.0*Math.sin(Math.toRadians(2.0*deltaTheta))*rc;
        double cFactor = delta_CPrime/sc;
        double hFactor = delta_HPrime/sh;
        return Math.sqrt(Math.pow(delta_LPrime/sl,2.0)+cFactor*cFactor+hFactor*hFactor+rt*cFactor*hFactor);
    }

    public static float[] toLab(Color col) {
        //RGB Normalization
        float[] vals = {col.getRed()/255.0f,col.getGreen()/255.0f,col.getBlue()/255.0f};

        //Conversion to CIEXYZ
        ColorSpace rgb = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        vals = rgb.toCIEXYZ(vals);

        //CIEXYZ to CIELAB
        vals[0]/=95.0489f;
        vals[1]/=100.0f;
        vals[2]/=108.884f;
        return new float[]{116.0f*f(vals[1])-16.0f, 500.0f*(f(vals[0])-f(vals[1])), 200.0f*(f(vals[1])-f(vals[2]))};
    }

    private static float f(float t)/*As described in the wikipedia article on CIELAB color space*/{
        float delta = 6.0f/29.0f;
        if(t > delta*delta*delta) return (float)Math.cbrt(t);
        return 1.0f/3.0f*t/(delta*delta)+4.0f/29.0f;
    }
}