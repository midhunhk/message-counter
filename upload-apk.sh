# Reference: https://github.com/the-dagger/open-event-android/blob/updateTools/upload-apk.sh

#create new directory to store the generated apk
mkdir $HOME/buildApk/

pwd
ls

#copy generated apk from build folder to the folder just created
cp -R app/build/outputs/apk/debugPro/debug/*.apk $HOME/buildApk/

#go to home and git setup  
cd $HOME
git config --global user.email "midhunhk@gmail.com"
git config --global user.name "midhunhk"

# Clone the repository in the folder buildApk
git clone --quiet --branch ci-builds https://midhunhk:$GITHUB_API_KEY@github.com/midhunhk/message-counter.git ci-builds > /dev/null

#go into directory and copy data we're interested
cd ci-builds 
cp -Rf $HOME/buildApk/* ./

#add, commit and push files
git add -f . 
#git remote rm origin 
#git remote add origin https://midhunhk:$GITHUB_API_KEY@github.com/midhunhk/message-counter.git
#git add -f .
git commit -m "Travis build $TRAVIS_BUILD_NUMBER pushed [skip ci]"
git push origin ci-builds -fq> /dev/null
echo -e "Done \ n "
