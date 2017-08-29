package istat.android.base.utils;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2014 Istat Dev.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 * @author Toukea Tatsi (Istat)
 *
 */
public class Matrice {
    int lignnb,columnb;
   String[][] m;
   String msg;
   
    public Matrice(int ls,int cs){

       m=new String[ls][cs];
       lignnb=ls;columnb=cs;
    }
    public String getmij(int i,int j){
     return m[i-1][j-1];
    }
    /*void setmij(int i,int j,String x){
      m[i-1][j-1]=String.valueOf(cal.subboot(x, cal.lect));
    }*/
    public void setmij(int i,int j,String x){
      m[i-1][j-1]=x;
    }
   public  double getmijdb(int i,int j) throws Exception{
     double tmp=Double.NaN;
     try
     {
       tmp=Double.valueOf(m[i-1][j-1]);
     }catch(Exception e){
    throw new Exception ("it is not a Valid number");
     }
        return tmp;
    }
   public  void setmijdb(int i,int j,double x){
      m[i-1][j-1]=String.valueOf(x);
    }
     public Integer getmijint(int i,int j){
     double tmp=Double.NaN;
     try
     {
       tmp=Double.valueOf(m[i-1][j-1]);
     }catch(Exception e){
     
     }
        return (int)tmp;
    }
   public  void setmijint(int i,int j,int x){
      m[i-1][j-1]=String.valueOf(x);
    }
    @Override
    public String toString(){
        return new MxManager().MStringtoString(this);
    }
    public  Matrice(String mx){
        Matrice mat=new MxManager().inserts(mx);
        m = mat.m;
        lignnb=mat.lignnb;columnb=mat.columnb;
    }
   public int getColumnNumber() {
	return columnb;
}
   public int getLineNumber() {
	return lignnb;
}

 public String MDeatiltoString(){
	 return new MxManager().MStringtoStringd(this);
 }
 public String[][]  getMatrixConstructor(){
	 return m;
 }
 class MxManager {

	boolean sro=false;
	
  String MStringtoString(Matrice m){
	       String buf="";
	       for(int i=1;i<=m.lignnb;i++){
	          for(int j=1;j<=m.columnb;j++){
	              buf=buf+m.getmij(i, j);
	             if(j<m.columnb) buf=buf+',';
	           }
	         if(i<m.lignnb) buf=buf+";";
	          }
	       return "["+buf+"]";
	   }
	   String MStringtoStringd(Matrice m){
	       String buf="";
	       for(int i=1;i<=m.lignnb;i++){
	          for(int j=1;j<=m.columnb;j++){
	        	  buf=buf+m.getmij(i, j);
	             if(j<m.columnb) buf=buf+"|";
	           }
	         if(i<m.lignnb) buf=buf+"\n";
	          }
	       return buf;
	   }



           //---------------------------------------------------------------------
             String MtoString(Matrice m){
	       String buf="";
	       for(int i=1;i<=m.lignnb;i++){
	          for(int j=1;j<=m.columnb;j++){
	              buf=buf+Float.valueOf(m.getmij(i, j));
	             if(j<m.columnb) buf=buf+',';
	           }
	         if(i<m.lignnb) buf=buf+";";
	          }
	       return "["+buf+"]";
	   }
	   String MtoStringd(Matrice m){
	       String buf="";
	       for(int i=1;i<=m.lignnb;i++){
	          for(int j=1;j<=m.columnb;j++){
	        	  buf=buf+Float.valueOf(m.getmij(i, j));
	             if(j<m.columnb) buf=buf+"|";
	           }
	         if(i<m.lignnb) buf=buf+"\n";
	          }
	       return buf;
	   }
 
	

	   Matrice inserts(String st){
		   String buf="";
		         List<String> vect= new ArrayList<String>();
		       int cmp=0;
		       int enfm=0;

		//*****************************************
		        int k=0;
		       Matrice mrr;
		      int l,c;
		      String[] tbf;
		        int enf=0,pos=0;
		//***********************************enfermement

		char[] ench=st.toCharArray();
		int frst=0;boolean frstk=false;
		       for(int i=1;i<=ench.length-2;i++){
		           if(ench[i]==','){
		               //enferm
		              // msgbox("jenferme "+buf);
		                 vect.add(buf);
		              // vect.add(buf);
		               buf="";
		               enf++;
		               enfm++;
		               if(!frstk) frst++;
		           }else if(ench[i]==';'){
		                  vect.add(buf);
		             // vect.add(buf);
		                 frstk=true;
		                buf="";
		                pos++;
		                enfm++;
		           }else{
		            buf=buf+ench[i];
		            // msgbox(""+buf);
		           }

		       }

		       if(((pos+1)*(frst+1))==(enfm+1)|| pos==0 || enf==0){
		   vect.add(buf);
		        //vect.add(buf);
		        enfm++;
		         tbf=new String [enfm];
		       while(cmp<vect.size()){
		            tbf[cmp]=vect.get(cmp);
		                    cmp++;
		        }






		//******************************************
		        //msgbox("enf "+enf+" "+pos);
		       l=pos+1;c=enfm/l;
		       mrr=new Matrice(l,c);
		       for(int i=1;i<=l;i++){
		           for(int j=1;j<=c;j++){
		                mrr.setmij(i, j,String.valueOf(tbf[k]));
		               // msgbox("a insere "+String.valueOf(tbf[k]));
		          // mrr.setmij(i, j,Double.valueOf(enm.nextElement().toString()));
		           k++;

		       }

		       }
		       //msgbox("prem "+ench[0]+" des  "+ench[ench.length-1]);
		        if(ench[0]!='['||ench[ench.length-1]!=']') return null;}else{ return null;}
		      return mrr;
		   }
  
    }

}
