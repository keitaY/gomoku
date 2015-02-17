package com.nazunasoft.gomoku;

import java.util.Random;

public class cpu {
	static int N=11;
	int[][] field = new int[N][N];
	int xb=0;
	int yb=0;
    int[] ans = new int[2];
	int p=0;
    
	public int[] cpu(int[][] fieldbuff){
		
	field = fieldbuff;
	int buff=0;
	int count=0;
	int blocked=0;
	xb=0;
	yb=0;
	p=0;
	ans[0]=-1;ans[1]=-1;
	//---------------------------------------------------------------------yoko
		for(int i=0;i<N;i++){
			buff=0;
			count=0;
			blocked=0;
			for(int j=0;j<N;j++){
				if(buff==0&&field[i][j]!=0)count++;
				if(buff!=0&&buff==field[i][j])count++;
				if(buff!=0&&field[i][j]!=0&&buff!=field[i][j]){count=1;blocked=1;}
				if(buff!=0&&field[i][j]==0){
					if(count==4){utu(i,j,4);}
					if(count==3&&blocked==0){utu(i,j,3);}
					if(count==2){utu(i,j,2);}
					count=0;
					blocked=0;
				}
				buff = field[i][j];	
			}
		}
		
		for(int i=0;i<N;i++){
			buff=0;
			count=0;
			blocked=0;
			for(int j=N-1;j>=0;j--){
				if(buff==0&&field[i][j]!=0)count++;
				if(buff!=0&&buff==field[i][j])count++;
				if(buff!=0&&field[i][j]!=0&&buff!=field[i][j]){count=1;blocked=1;}
				if(buff!=0&&field[i][j]==0){
					if(count==4){utu(i,j,4);}
					if(count==3&&blocked==0){utu(i,j,3);}
					if(count==2){utu(i,j,2);}
					count=0;
					blocked=0;
				}
				buff = field[i][j];	
			}
		}
	//----------------------------------------------------------------------tate
		for(int i=0;i<N;i++){
			buff=0;
			count=0;
			blocked=0;
			for(int j=0;j<N;j++){
				if(buff==0&&field[j][i]!=0)count++;
				if(buff!=0&&buff==field[j][i])count++;
				if(buff!=0&&field[j][i]!=0&&buff!=field[j][i]){count=1;blocked=1;}
				if(buff!=0&&field[j][i]==0){
					if(count==4){utu(j,i,4);}
					if(count==3&&blocked==0){utu(j,i,3);}
					if(count==2){utu(j,i,2);}
					count=0;
					blocked=0;
				}
				buff = field[j][i];	
			}
		}
		

		for(int i=0;i<N;i++){
			buff=0;
			count=0;
			blocked=0;
			for(int j=N-1;j>=0;j--){
				if(buff==0&&field[j][i]!=0)count++;
				if(buff!=0&&buff==field[j][i])count++;
				if(buff!=0&&field[j][i]!=0&&buff!=field[j][i]){count=1;blocked=1;}
				if(buff!=0&&field[j][i]==0){
					if(count==4){utu(j,i,4);}
					if(count==3&&blocked==0){utu(j,i,3);}
					if(count==2){utu(j,i,2);}
					count=0;
					blocked=0;
				}
				buff = field[j][i];	
			}
		}
		
		//------------------------------------------------------------------------naname
		int k=0;int l=0;int a=0;int b=0;
		
		for(a=0;a<N;a++){
			buff=0;k=0;l=0;b=0;
			count=0;blocked=0;
			for(;a+k>=0&&a+k<N&&b+l>=0&&b+l<N;){
				if(buff==0&&field[a+k][b+l]!=0)count++;
				if(buff!=0&&buff==field[a+k][b+l])count++;
				if(buff!=0&&field[a+k][b+l]!=0&&buff!=field[a+k][b+l]){count=1;blocked=1;}
				if(buff!=0&&field[a+k][b+l]==0){
					if(count==4){utu(a+k,b+l,4);}
					if(count==3&&blocked==0){utu(a+k,b+l,3);}
					if(count==2){utu(a+k,b+l,2);}
					count=0;blocked=0;
				}
				buff = field[a+k][b+l];
				k++;l++;
			}
		}
		
		
		for(b=0;b<N;b++){
			buff=0;k=0;l=0;a=0;
			count=0;blocked=0;
			for(;a+k>=0&&a+k<N&&b+l>=0&&b+l<N;){
				if(buff==0&&field[a+k][b+l]!=0)count++;
				if(buff!=0&&buff==field[a+k][b+l])count++;
				if(buff!=0&&field[a+k][b+l]!=0&&buff!=field[a+k][b+l]){count=1;blocked=1;}
				if(buff!=0&&field[a+k][b+l]==0){
					if(count==4){utu(a+k,b+l,4);}
					if(count==3&&blocked==0){utu(a+k,b+l,3);}
					if(count==2){utu(a+k,b+l,2);}
					count=0;blocked=0;
				}
				buff = field[a+k][b+l];
				k++;l++;
			}
		}
		
		/*
		for(a=0;a<N;a++){
			buff=0;k=N-1;l=N-1;b=N-1;
			count=0;blocked=0;
			for(;a+k>=0&&a+k<N&&b+l>=0&&b+l<N;){
				if(buff==0&&field[a+k][b+l]!=0)count++;
				if(buff!=0&&buff==field[a+k][b+l])count++;
				if(buff!=0&&field[a+k][b+l]!=0&&buff!=field[a+k][b+l]){count=1;blocked=1;}
				if(buff!=0&&field[a+k][b+l]==0){
					if(count==4){utu(k,l,4);}
					if(count==3&&blocked==0){utu(k,l,3);}
					if(count==2){utu(k,l,2);}
					count=0;blocked=0;
				}
				buff = field[a+k][b+l];
				k--;l--;
			}
		}
		
		for(b=0;b<N;b++){
			buff=0;k=N-1;l=N-1;a=N-1;
			count=0;blocked=0;
			for(;a+k>=0&&a+k<N&&b+l>=0&&b+l<N;){
				if(buff==0&&field[a+k][b+l]!=0)count++;
				if(buff!=0&&buff==field[a+k][b+l])count++;
				if(buff!=0&&field[a+k][b+l]!=0&&buff!=field[a+k][b+l]){count=1;blocked=1;}
				if(buff!=0&&field[a+k][b+l]==0){
					if(count==4){utu(k,l,4);}
					if(count==3&&blocked==0){utu(k,l,3);}
					if(count==2){utu(k,l,2);}
					count=0;blocked=0;
				}
				buff = field[a+k][b+l];
				k--;l--;
			}
		}
		

		for(a=N-1;a>=0;a--){
			buff=0;k=0;l=0;b=0;
			count=0;blocked=0;
			for(;a+k>=0&&a+k<N&&b+l>=0&&b+l<N;){
				if(buff==0&&field[a+k][b+l]!=0)count++;
				if(buff!=0&&buff==field[a+k][b+l])count++;
				if(buff!=0&&field[a+k][b+l]!=0&&buff!=field[a+k][b+l]){count=1;blocked=1;}
				if(buff!=0&&field[a+k][b+l]==0){
					if(count==4){utu(k,l,4);}
					if(count==3&&blocked==0){utu(k,l,3);}
					if(count==2){utu(k,l,2);}
					count=0;blocked=0;
				}
				buff = field[a+k][b+l];
				k--;l++;
			}
		}
		
		for(b=0;b<N;b++){
			buff=0;k=0;l=0;a=N-1;
			count=0;blocked=0;
			for(;a+k>=0&&a+k<N&&b+l>=0&&b+l<N;){
				if(buff==0&&field[a+k][b+l]!=0)count++;
				if(buff!=0&&buff==field[a+k][b+l])count++;
				if(buff!=0&&field[a+k][b+l]!=0&&buff!=field[a+k][b+l]){count=1;blocked=1;}
				if(buff!=0&&field[a+k][b+l]==0){
					if(count==4){utu(k,l,4);}
					if(count==3&&blocked==0){utu(k,l,3);}
					if(count==2){utu(k,l,2);}
					count=0;blocked=0;
				}
				buff = field[a+k][b+l];
				k--;l++;
			}
		}
		
		for(a=0;a<N;a--){
			buff=0;k=0;l=0;b=N-1;
			count=0;blocked=0;
			for(;a+k>=0&&a+k<N&&b+l>=0&&b+l<N;){
				if(buff==0&&field[a+k][b+l]!=0)count++;
				if(buff!=0&&buff==field[a+k][b+l])count++;
				if(buff!=0&&field[a+k][b+l]!=0&&buff!=field[a+k][b+l]){count=1;blocked=1;}
				if(buff!=0&&field[a+k][b+l]==0){
					if(count==4){utu(k,l,4);}
					if(count==3&&blocked==0){utu(k,l,3);}
					if(count==2){utu(k,l,2);}
					count=0;blocked=0;
				}
				buff = field[a+k][b+l];
				k++;l--;
			}
		}
		
		for(b=0;b<N;b++){
			buff=0;k=0;l=0;a=0;
			count=0;blocked=0;
			for(;a+k>=0&&a+k<N&&b+l>=0&&b+l<N;){
				if(buff==0&&field[a+k][b+l]!=0)count++;
				if(buff!=0&&buff==field[a+k][b+l])count++;
				if(buff!=0&&field[a+k][b+l]!=0&&buff!=field[a+k][b+l]){count=1;blocked=1;}
				if(buff!=0&&field[a+k][b+l]==0){
					if(count==4){utu(k,l,4);}
					if(count==3&&blocked==0){utu(k,l,3);}
					if(count==2){utu(k,l,2);}
					count=0;blocked=0;
				}
				buff = field[a+k][b+l];
				k++;l--;
			}
		}
		*/

		if(ans[0]!=-1&&ans[1]!=-1){
			return ans;
		}else{
			while(true){
			Random rnd = new Random();
			xb = rnd.nextInt(N);
			yb = rnd.nextInt(N);
			if(field[xb][yb]==0){utu(xb,yb,1);break;}
			}
			return ans;
			}
		
	}
	
	public void utu(int gx,int gy, int prior){
		if (p < prior){
		p=prior;
		ans[0]=gx;
		ans[1]=gy;
		}
	}
}
